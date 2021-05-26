package com.transtour.backend.voucher.repository;


import com.transtour.backend.voucher.model.Voucher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Qualifier("VoucherRepo")
@Repository
public interface IVoucherRepository extends MongoRepository<Voucher,String> {

    Optional<Voucher> findByTravelId(String travelId);

}
