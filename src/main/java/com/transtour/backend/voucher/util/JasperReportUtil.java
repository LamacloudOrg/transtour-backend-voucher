package com.transtour.backend.voucher.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class JasperReportUtil {

    public static String generate(String fileName,  ArrayList<Map<String, Object>> pieceFieldDetailsMaps,Map pieceDetailsMap ) throws Exception{
        File jasper = ResourceUtils.getFile(VoucherUtil.jasperFile);

        JasperReport jasperReport = JasperCompileManager.compileReport(jasper.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, VoucherUtil.path + fileName);

        File pdf = new File(VoucherUtil.path + fileName);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdf));

        byte[] bytes = Files.readAllBytes(pdf.toPath());

        String b64 = Base64.getEncoder().encodeToString(bytes);

        return b64;
    }

    public static Long fileLength(String fileName) throws Exception{

        return  new File(VoucherUtil.path + fileName).length();

    }
}
