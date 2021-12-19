package com.transtour.backend.voucher.repository;

import com.transtour.backend.voucher.model.SignatureVoucher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Qualifier("SignatureVoucherRepo")
@Repository
public interface ISignatureVoucherRepository extends MongoRepository<SignatureVoucher, Long> {
}