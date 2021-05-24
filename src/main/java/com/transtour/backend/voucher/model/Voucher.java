package com.transtour.backend.voucher.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Data
@QueryEntity
@Document("voucher")
public class Voucher implements Serializable {
    @Id
    String id;
    String travelId;
    String voucher;
    String documentSigned;
    @CreatedDate
    LocalDateTime crate_at;
}

