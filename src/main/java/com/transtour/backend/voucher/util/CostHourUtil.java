package com.transtour.backend.voucher.util;

import com.transtour.backend.voucher.dto.TravelDTO;
import com.transtour.backend.voucher.model.Company;

public class CostHourUtil {

    public  static Double calculateCost(TravelDTO travelDTO, Company company){

        String hora = travelDTO.whitingTime.substring(0, 2);
        int cantHoras = Integer.parseInt(hora);

        String minuto = travelDTO.whitingTime.substring(3, 5);
        int cantMinutos = Integer.parseInt(minuto);

     // String precio = travelDTO.whitingTime.substring(6);
     // Double valorHora = Double.parseDouble(precio);
        Double valorHora = company.getWhitingTimeAmount();

        Double precioEsperaNumerico = 0.0;
        Double precioMinutos = 0.0;

        if (cantHoras != 0) {
            precioEsperaNumerico = valorHora * cantHoras;
        }
        precioMinutos = calcularMinutos(cantMinutos, valorHora);

        Double total = precioEsperaNumerico + precioMinutos;
        return total;

    }


    public static Double calcularMinutos(int cantMinutos, double valorHora) {
        switch (cantMinutos) {
            case 0:
                return 0.0;
            case 15:
                return (valorHora / 4);
            case 30:
                return (valorHora / 2);
            case 45:
                return (valorHora / 4) + (valorHora / 2);
        }
        return valorHora;
    }
}
