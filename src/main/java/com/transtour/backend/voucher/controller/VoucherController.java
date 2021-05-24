package com.transtour.backend.voucher.controller;

import com.transtour.backend.voucher.service.VoucherService;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.annotation.MultipartConfig;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RequestMapping(path = "/v1/voucher")
@RestController
public class VoucherController {

    @Autowired
    VoucherService service;

    @GetMapping
    public String health(){
        return "working";
    }

    @PostMapping("/create")
    public String create(@RequestBody String travelId) throws FileNotFoundException, JRException {
        return service.create(travelId);
    }

    /**
     *  <p>permitira subir la  la firma en base 64</p>
     * @param file
     * @return
     */
    @PostMapping("/upload/{travelId}")
    public String singleFileUpload(@PathVariable("travelId") String travelId, @RequestBody String file ) {
        service.uploadFile(travelId,file);
        return "file uploaded";
    }

    @GetMapping("/downloadPdf/{id}")
    public String generatePdf(@PathVariable("id") String voucherId) throws FileNotFoundException, JRException {
        return service.exportVoucher(voucherId);
    }


}
