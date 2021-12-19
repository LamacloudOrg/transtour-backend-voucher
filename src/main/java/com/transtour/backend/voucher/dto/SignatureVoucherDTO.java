package com.transtour.backend.voucher.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class SignatureVoucherDTO implements Serializable {

    private Long travelId;
    private String base64;
    private String contentType;
}
