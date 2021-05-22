package com.transtour.backend.voucher.controller;

import com.transtour.backend.voucher.service.VoucherService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.FileNotFoundException;

@RequestMapping(path = "/v1/voucher")
@RestController
public class VoucherController {

    @Autowired
    VoucherService service;

    @GetMapping
    public String health(){
        return "working";
    }

    @GetMapping("/downloadPdf/{id}")
    public String generatePdf(@PathVariable("id") Long voucherId) throws FileNotFoundException, JRException {
        return service.exportVoucher(voucherId);
    }

}
