package com.transtour.backend.voucher.model;


import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class Voucher implements Serializable {

    Long id; //orden de servicio
    LocalDateTime createDate;
    String car; // Hacer un objeto car con sos atributos
    String carDriver;
    LocalTime startTime;

    String company; // crear objeto empresa con sus atributos
    String bc;

    String passenger;
    Long reserveNumber;

    String originAddress;
    String destinyAddress;

    String observation;
    String amount;
    LocalDate whitingTime;
    LocalTime peajes;
    Double parkingAmount;
    Double taxForReturn;
    Double totalAmount;
    File signatur;
}
