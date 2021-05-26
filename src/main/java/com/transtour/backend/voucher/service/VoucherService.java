package com.transtour.backend.voucher.service;


import com.github.dozermapper.core.Mapper;
import com.transtour.backend.voucher.model.Travel;
import com.transtour.backend.voucher.dto.TravelDTO;
import com.transtour.backend.voucher.model.Voucher;
import com.transtour.backend.voucher.model.VoucherStatus;
import com.transtour.backend.voucher.repository.ITravelRepo;
import com.transtour.backend.voucher.repository.IVoucherRepository;
import com.transtour.backend.voucher.util.VoucherUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherService.class);

    @Qualifier("VoucherRepo")
    @Autowired
    IVoucherRepository voucerRepo;

    @Qualifier("TravelRepo")
    @Autowired
    ITravelRepo travelRepo;


    @Autowired
    Mapper mapper;


    public CompletableFuture<String> exportVoucher(String voucherId) throws FileNotFoundException, JRException {
        //TODO agregar regla que solo se permita genera el voucher si esta en estado ready

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                ()->{
                ArrayList<Map<String, Object>> pieceFieldDetailsMaps = new ArrayList<Map<String, Object>>();

                Optional<Voucher> voucher = voucerRepo.findById(voucherId);

                voucher.orElseThrow(RuntimeException::new);

                Travel travel = travelRepo.getTravel(voucher.get().getTravelId());

                TravelDTO travelDTO = new TravelDTO();

                mapper.map(travel,travelDTO);

                Map pieceDetailsMap = VoucherUtil.mapDetail(travelDTO);

                pieceFieldDetailsMaps.add(pieceDetailsMap);

                try {
                    File file = ResourceUtils.getFile(VoucherUtil.jasperFile);
                    JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
                    JasperExportManager.exportReportToPdfFile(jasperPrint, VoucherUtil.path);

                }catch (Exception e){
                    throw new RuntimeException(e);
                }
                return "OK";
        });
        return completableFuture;
    }

    public CompletableFuture<String> create(Travel travel) {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                ()->{
                    Voucher v = new Voucher();
                    v.setTravelId(travel.getOrderNumber());
                    v.setCompany(travel.getCompany());
                    v.setDateCreated(LocalDate.now());
                    v.setTime(LocalDateTime.now());
                    v.setStatus(VoucherStatus.CREATED);
                    voucerRepo.save(v);
                    return v.getId();
                });
        return completableFuture;
    }

    public CompletableFuture<String> uploadFile(String travelId, String file) {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
        ()->{
            Optional<Voucher> voucher = voucerRepo.findByTravelId(travelId);
            voucher.orElseThrow(RuntimeException::new);
            voucher.get().setDocumentSigned(file);
            voucher.get().setStatus(VoucherStatus.READY);
            voucerRepo.save(voucher.get());
            return "file was uploaded";
        });
        return completableFuture;
    }
}
