package com.transtour.backend.voucher.controller;

import com.transtour.backend.voucher.model.Travel;
import com.transtour.backend.voucher.model.VoucherStatus;
import com.transtour.backend.voucher.service.VoucherService;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import java.util.concurrent.CompletableFuture;

@RequestMapping(path = "/v1/voucher")
@RestController
public class VoucherController {

    @Autowired
    VoucherService service;

    @GetMapping
    public String health(){
        return "working";
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/create")
    public CompletableFuture<String> create(@RequestBody Travel travel)  {
        return service.create(travel);
    }

    /**
     *  <p>permitira subir la  la firma en base 64</p>
     * @param file
     * @return
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/upload/{travelId}")
    public CompletableFuture<String> singleFileUpload(@PathVariable("travelId") String travelId, @RequestBody String file ) {
        return service.uploadFile(travelId,file);
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/downloadPdf/{id}")
    public CompletableFuture<String> generatePdf(@PathVariable("id") String voucherId) throws FileNotFoundException, JRException {
        return service.exportVoucher(voucherId);
    }

}
