package com.transtour.backend.voucher.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@QueryEntity
@Document("voucher")
public class Voucher implements Serializable {

    Long travelId;
    VoucherStatus status;
    String voucher;
    String documentSigned;
    String company;
    @CreatedDate
    LocalDate dateCreated;
    LocalDateTime time;
}

