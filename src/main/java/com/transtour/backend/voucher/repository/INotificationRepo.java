package com.transtour.backend.voucher.repository;

import com.transtour.backend.voucher.dto.NotificationVoucherDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Qualifier("NotificationRepo")
@FeignClient(name = "SPRING-CLOUD-NOTIFICATION")
public interface INotificationRepo {

    @RequestMapping(method = RequestMethod.POST, value = "/v1/notification/sendPdfToPassenger")
    Void sendPdfToPassenger(@RequestBody NotificationVoucherDTO notificationVoucherDTO);

}