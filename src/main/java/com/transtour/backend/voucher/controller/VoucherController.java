package com.transtour.backend.voucher.controller;


import com.transtour.backend.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping(path = "/v1/voucher")
@RestController
public class VoucherController {

    @Autowired
    VoucherService service;

    @GetMapping
    public String health(){
        return "working";
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> generateVocuher(@PathVariable("id") String voucherId) throws Exception{
        service.getVoucher(Integer.valueOf(voucherId));
        return  new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
