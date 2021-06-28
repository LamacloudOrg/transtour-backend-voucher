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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
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


    public CompletableFuture<ResponseEntity> exportVoucher(String voucherId) throws FileNotFoundException, JRException {
        //TODO agregar regla que solo se permita genera el voucher si esta en estado ready

        CompletableFuture<ResponseEntity> completableFuture = CompletableFuture.supplyAsync(
                ()->{
                ArrayList<Map<String, Object>> pieceFieldDetailsMaps = new ArrayList<Map<String, Object>>();

                Optional<Voucher> voucher = voucerRepo.findByTravelId(voucherId);

                voucher.orElseThrow(RuntimeException::new);

                Travel travel = travelRepo.getTravel(voucher.get().getTravelId());

                TravelDTO travelDTO = new TravelDTO();

                mapper.map(travel,travelDTO);

                Map pieceDetailsMap = VoucherUtil.mapDetail(travelDTO);

                pieceFieldDetailsMaps.add(pieceDetailsMap);
                String fileName = travelDTO.dateCreated.toString() + "-" +travelDTO.time.toString() + "-"+ travelDTO.company.toString() + "-" + travelDTO.orderNumber.toString();

                try {
                    File jasper = ResourceUtils.getFile(VoucherUtil.jasperFile);
                   // Resource r = new ClassPathResource(VoucherUtil.jasperFile);
                    //File jasper = r.getFile();

                    JasperReport jasperReport = JasperCompileManager.compileReport(jasper.getAbsolutePath());
                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
                    JasperExportManager.exportReportToPdfFile(jasperPrint, VoucherUtil.path + fileName);

                    //Thread.sleep(1500);

                    File pdf = new File(VoucherUtil.path + fileName);


                    InputStreamResource resource = new InputStreamResource(new FileInputStream(pdf));

                    byte [] bytes = Files.readAllBytes(pdf.toPath());

                    String b64 = Base64.getEncoder().encodeToString(bytes);

                    voucher.get().setVoucher(b64);
                    voucerRepo.save(voucher.get());

                    return ResponseEntity.ok()
                            // Content-Disposition
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pdf.getName())
                            // Content-Type
                            .contentType(MediaType.APPLICATION_PDF)
                            // Contet-Length
                            .contentLength(pdf.length()) //
                            .body(resource);

                }catch (Exception e){
                    throw new RuntimeException(e);
                }
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

    public CompletableFuture<ResponseEntity> findAll(Pageable pageable) {

        CompletableFuture<ResponseEntity> completableFuture  = CompletableFuture.supplyAsync( ()-> {
            Page<Voucher> page = voucerRepo.findAll(pageable);
            return ResponseEntity.ok().body(page);

        });

        return completableFuture;
    }
}
