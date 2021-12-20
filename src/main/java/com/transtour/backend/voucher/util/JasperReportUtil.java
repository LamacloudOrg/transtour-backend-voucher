package com.transtour.backend.voucher.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class JasperReportUtil {

    public static void generate(String fileName,  ArrayList<Map<String, Object>> pieceFieldDetailsMaps,Map pieceDetailsMap ) throws Exception{
        File jasper = ResourceUtils.getFile(VoucherUtil.jasperFile);

        JasperReport jasperReport = JasperCompileManager.compileReport(jasper.getAbsolutePath());
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
        JasperExportManager.exportReportToPdfFile(jasperPrint, VoucherUtil.path + fileName);

    }


}
