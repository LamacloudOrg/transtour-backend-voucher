package com.transtour.backend.voucher.service;


import com.github.dozermapper.core.Mapper;
import com.transtour.backend.voucher.dto.Travel;
import com.transtour.backend.voucher.dto.TravelDTO;
import com.transtour.backend.voucher.model.Voucher;
import com.transtour.backend.voucher.repository.ITravelRepo;
import com.transtour.backend.voucher.repository.IVoucherRepository;
import com.transtour.backend.voucher.util.VoucherUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

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


    public String exportVoucher(String voucherId) throws FileNotFoundException, JRException {

        ArrayList<Map<String, Object>> pieceFieldDetailsMaps = new ArrayList<Map<String, Object>>();

        Optional<Voucher> voucher = voucerRepo.findById(voucherId);

        voucher.orElseThrow(RuntimeException::new);

        Travel travel = travelRepo.getTravel(voucher.get().getTravelId());

        TravelDTO travelDTO = new TravelDTO();

        mapper.map(travel,travelDTO);

        Map pieceDetailsMap = VoucherUtil.mapDetail(travelDTO);

        pieceFieldDetailsMaps.add(pieceDetailsMap);

        File file = ResourceUtils.getFile(VoucherUtil.jasperFile);
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, VoucherUtil.path);
        return "OK";
    }

    public String create(String travelId) {
        Voucher v = new Voucher();
        v.setTravelId(travelId);
        v.setCrate_at(LocalDateTime.now());
        return "OK";
    }

    public void uploadFile(String travelId, String file) {

        Optional<Voucher> voucher = voucerRepo.findByTravelId(travelId);
        voucher.orElseThrow(RuntimeException::new);
        voucher.get().setDocumentSigned(file);
        voucerRepo.save(voucher.get());
    }
}
