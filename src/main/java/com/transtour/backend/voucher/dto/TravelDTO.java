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

    public String id; //orden de servicio
    public LocalDate createDate;
    public String car; // Hacer un objeto car con sos atributos
    public String carDriver;
    public LocalTime startTime;

    public String company; // crear objeto empresa con sus atributos
    public String bc;

    public String passenger;
    public Long reserveNumber;

    public String originAddress;
    public String destinyAddress;

    public String observation;
    public String amount;
    public LocalDate whitingTime;
    public LocalTime peajes;
    public Double parkingAmount;
    public Double taxForReturn;
    public Double totalAmount;
    public File signatur;
}
