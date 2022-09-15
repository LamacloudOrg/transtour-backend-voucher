package com.transtour.backend.voucher.repository;

import com.transtour.backend.voucher.model.Company;
import feign.Param;
import feign.RequestLine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Qualifier("CompanyRepo")
@FeignClient(name = "SPRING-CLOUD-USER-API")
public interface ICompanyRepository {

    /*
    @RequestMapping(method= RequestMethod.GET,value = "/v1/company?fullName")
    Company getCompany(@PathVariable String fullName);
    */

    @RequestLine("GET /v1/company?fullName={fullName}")
    Company getCompany(@Param(value = "fullName") String fullName);
}