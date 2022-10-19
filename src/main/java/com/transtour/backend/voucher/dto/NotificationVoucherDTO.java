package com.transtour.backend.voucher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class NotificationVoucherDTO implements Serializable {
    private Long travelId;
    private String passengerEmail;
}