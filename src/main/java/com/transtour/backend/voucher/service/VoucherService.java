package com.transtour.backend.voucher.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.itextpdf.html2pdf.HtmlConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherService.class);


    public void getVoucher(Integer id) throws  Exception{
        Map<String,Object> travel = new HashMap<>();
        travel.put("nombre","Juan Manuel");
        travel.put("fecha", LocalDate.now().toString());
        travel.put("hora", LocalTime.now().toString());

        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache m = mf.compile("template/v1.html");

        StringWriter writer = new StringWriter();
        m.execute(writer, travel).flush();
        String html = writer.toString();

        String fileName= "./pdf/"+id + ".pdf";

        File f = new File(fileName);

        HtmlConverter.convertToPdf(html, new FileOutputStream(f));

        LOG.debug("Se creo el archivo :"+ fileName);

    }

}
