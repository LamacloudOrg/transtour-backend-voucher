package com.transtour.backend.voucher.excption;

public class VoucherNotReady extends RuntimeException{

    public VoucherNotReady(){
        super("Voucher is not ready");
    }
}
