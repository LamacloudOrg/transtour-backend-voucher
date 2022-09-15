package com.transtour.backend.voucher.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Company {

    private Long id;
    private String fullName;
    private String nickName;
    private String cuit;
    private Double email;
    private String phone;
    private Double whitingTimeAmount;
    private String dispositionTimeAmount;
}