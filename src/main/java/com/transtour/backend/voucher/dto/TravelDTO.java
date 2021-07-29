package com.transtour.backend.voucher.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Data
public class TravelDTO implements Serializable {
    public String orderNumber;
    public LocalDate dateCreated;
    public String car;
    public String carDriver;
    public LocalTime time;
    public String company;
    public String bc;
    public String passenger;
    public String reserveNumber;
    public String originAddress;
    public String destinyAddress;
    public String observation;
    public String amount;
    public String whitingTime;
    public String toll;
    public String parkingAmount;
    public String taxForReturn;
    public String totalAmount;
    public String signature;
}
