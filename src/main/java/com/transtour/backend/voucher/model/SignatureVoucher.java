package com.transtour.backend.voucher.model;

import com.querydsl.core.annotations.QueryEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

@NoArgsConstructor
@Data
@QueryEntity
@Document("signatureVoucher")
public class SignatureVoucher implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String travelId;

    @Lob
    private String base64;
    private String contentType;
}
