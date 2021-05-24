package com.transtour.backend.voucher.dto;

import com.github.dozermapper.core.Mapping;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@Data
public class Travel {
    String id;
    @Mapping("carDriver")
    String chofer;
    @Mapping("passenger")
    String pasajero;
    @Mapping("createDate")
    LocalDate fecha;
    @Mapping("startTime")
    LocalTime hora;
    @Mapping("destinyAddress")
    String direccion;
    LocalDateTime crate_at;

}
