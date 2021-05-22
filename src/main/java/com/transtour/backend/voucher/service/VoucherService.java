package com.transtour.backend.voucher.service;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherService.class);
   private ArrayList<Map<String, Object>> pieceFieldDetailsMaps;
    String path = "./src/main/resources/voucher/voucher.pdf";

    public String exportVoucher(Long voucherId) throws FileNotFoundException, JRException {

        pieceFieldDetailsMaps = new ArrayList<Map<String, Object>>();

        Map<String, Object> pieceDetailsMap = new HashMap<String, Object>();
        pieceDetailsMap.put("orderNumber","123456");
        pieceDetailsMap.put("dateCreated","21/05/2021");
        pieceDetailsMap.put("car","Toyota");
        pieceDetailsMap.put("time","14:30");
        pieceDetailsMap.put("carDriver","Quique");
        pieceDetailsMap.put("company","MercadoLibre");
        pieceDetailsMap.put("passenger","Maradona");
        pieceDetailsMap.put("originAddress","calle 1");
        pieceDetailsMap.put("destinyAddress","Calle 10");
        pieceDetailsMap.put("observations","No aplica");
        pieceDetailsMap.put("amount","1399,99");
        pieceDetailsMap.put("whitingTime","3 horas");
        pieceDetailsMap.put("toll","123,88");
        pieceDetailsMap.put("taxParking","223");
        pieceDetailsMap.put("taxForBackCompany","000");
        pieceDetailsMap.put("totalAmount","2500");
        pieceDetailsMap.put("bc","---");
        pieceDetailsMap.put("reserveNumber","100");

        this.pieceFieldDetailsMaps.add(pieceDetailsMap);

        File file = ResourceUtils.getFile("classpath:jasperReport/voucher.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(file.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, path);
        return "OK";
    }
}
