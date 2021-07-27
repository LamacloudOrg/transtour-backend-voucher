package com.transtour.backend.voucher.controller;

import com.transtour.backend.voucher.dto.SignatureVoucherDTO;
import com.transtour.backend.voucher.model.SignatureVoucher;
import com.transtour.backend.voucher.model.Travel;
import com.transtour.backend.voucher.service.VoucherService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.FileNotFoundException;
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
    public CompletableFuture<ResponseEntity> generatePdf(@PathVariable("id") String voucherId) throws FileNotFoundException, JRException {
        return service.exportVoucher(voucherId);
    }

    @GetMapping("/list")
    public CompletableFuture<ResponseEntity> list (Pageable pageable) throws Exception {
        return service.findAll(pageable);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/saveSignature")
    public CompletableFuture<String> saveSignature(@RequestBody SignatureVoucherDTO signatureVoucherDTO)  {
        return service.saveSignatureVoucher(signatureVoucherDTO);
    }

}
