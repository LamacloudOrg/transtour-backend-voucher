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
    private Double dispositionTimeAmount;

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", cuit='" + cuit + '\'' +
                ", email=" + email +
                ", phone='" + phone + '\'' +
                ", whitingTimeAmount=" + whitingTimeAmount +
                ", dispositionTimeAmount=" + dispositionTimeAmount +
                '}';
    }
}