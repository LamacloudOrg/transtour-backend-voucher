package com.transtour.backend.voucher.util;

import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VoucherUtil {

    public static String path = "/opt/app/voucher/";
    public static String jasperFile = "/opt/app/voucher.jrxml";

    public static Map<String, Object> mapDetail(@NonNull Object o) {
        Map<String, Object> detail = new HashMap<>();
        Field[] fields = o.getClass().getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            try {
                detail.put(field.getName(), Optional.ofNullable(field.get(o).toString()).orElse(""));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        return detail;
    }

    private void voucherExample() {
        Map<String, Object> pieceDetailsMap = new HashMap<String, Object>();
        pieceDetailsMap.put("orderNumber", "123456");
        pieceDetailsMap.put("dateCreated", "21/05/2021");
        pieceDetailsMap.put("car", "Toyota");
        pieceDetailsMap.put("time", "14:30");
        pieceDetailsMap.put("carDriver", "Quique");
        pieceDetailsMap.put("company", "MercadoLibre");
        pieceDetailsMap.put("passenger", "Maradona");
        pieceDetailsMap.put("originAddress", "calle 1");
        pieceDetailsMap.put("destinyAddress", "Calle 10");
        pieceDetailsMap.put("observations", "No aplica");
        pieceDetailsMap.put("amount", "1399,99");
        pieceDetailsMap.put("whitingTime", "3 horas");
        pieceDetailsMap.put("toll", "123,88");
        pieceDetailsMap.put("taxParking", "223");
        pieceDetailsMap.put("taxForBackCompany", "000");
        pieceDetailsMap.put("totalAmount", "2500");
        pieceDetailsMap.put("bc", "---");
        pieceDetailsMap.put("reserveNumber", "100");
    }
}
