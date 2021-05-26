package com.transtour.backend.voucher.repository;

import com.transtour.backend.voucher.model.Travel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Qualifier("TravelRepo")
@FeignClient(name = "SPRING-CLOUD-TRAVEL-API")
public interface ITravelRepo {

    @RequestMapping(method= RequestMethod.GET,value = "/v1/travel/{travelId}")
    Travel getTravel(@PathVariable String travelId);
}
