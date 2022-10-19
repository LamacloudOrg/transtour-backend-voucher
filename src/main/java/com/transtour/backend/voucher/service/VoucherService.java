package com.transtour.backend.voucher.service;

import com.github.dozermapper.core.Mapper;
import com.transtour.backend.voucher.dto.NotificationVoucherDTO;
import com.transtour.backend.voucher.dto.SignatureVoucherDTO;
import com.transtour.backend.voucher.dto.TravelDTO;
import com.transtour.backend.voucher.excption.VoucherNotReady;
import com.transtour.backend.voucher.model.*;
import com.transtour.backend.voucher.repository.*;
import com.transtour.backend.voucher.util.CostHourUtil;
import com.transtour.backend.voucher.util.ImageUtil;
import com.transtour.backend.voucher.util.JasperReportUtil;
import com.transtour.backend.voucher.util.VoucherUtil;
import net.sf.jasperreports.engine.*;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.io.*;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherService.class);

    @Qualifier("VoucherRepo")
    @Autowired
    IVoucherRepository voucerRepo;

    @Qualifier("TravelRepo")
    @Autowired
    ITravelRepo travelRepo;

    @Qualifier("SignatureVoucherRepo")
    @Autowired
    ISignatureVoucherRepository iSignatureRepo;

    @Qualifier("CompanyRepo")
    @Autowired
    ICompanyRepository companyRepo;

    @Autowired
    @Qualifier("NotificationRepo")
    private INotificationRepo notificationRepo;

    @Autowired
    Mapper mapper;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    public CompletableFuture<ResponseEntity> exportVoucher(Long voucherId) throws FileNotFoundException, JRException {
        //TODO agregar regla que solo se permita genera el voucher si esta en estado ready

        CompletableFuture<ResponseEntity> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    ArrayList<Map<String, Object>> pieceFieldDetailsMaps = new ArrayList<Map<String, Object>>();

                    Optional<Voucher> voucher = voucerRepo.findByTravelId(Long.valueOf(voucherId));

                    voucher.orElseThrow(RuntimeException::new);

                    if (!voucher.get().getStatus().equals(VoucherStatus.READY)) throw new VoucherNotReady();

                    Travel travel = travelRepo.getTravel(voucher.get().getTravelId());

                    TravelDTO travelDTO = new TravelDTO();

                    travelDTO.setReserveNumber(travel.getReserveNumber());
                    mapper.map(travel, travelDTO);
                    String document = voucher.get().getDocumentSigned();
                    String signatureFile = VoucherUtil.path + travel.getOrderNumber();

                    try {
                        ImageIO.write(ImageUtil.convertToBufferedImage(ImageUtil.resizeImage(document)), "jpg", new File(signatureFile));
                    } catch (Exception e) {
                        throw new RuntimeException(e.getCause());
                    }

                    LOG.debug("file " + signatureFile + "exist " + FileUtils.getFile(signatureFile).exists());

                    travelDTO.setSignature(signatureFile);

                    String hora = travelDTO.whitingTime.substring(0, 2);
                 // int cantHoras = Integer.parseInt(hora);
                    String minuto = travelDTO.whitingTime.substring(3, 5);
                 // int cantMinutos = Integer.parseInt(minuto);

                    LOG.info("que tiene voucher.get().getCompany(): " + voucher.get().getCompany().toString());
                    Company company = companyRepo.getCompany(voucher.get().getCompany());
                    LOG.info("que tiene Company: " + company.toString());

                    Double total = CostHourUtil.calculateCost(travelDTO, company);
                    travelDTO.setWhitingTime(total.toString());
                    travelDTO.setHours(hora + " : " + minuto);

                    Double resultAmount = (Double.parseDouble(travelDTO.amount) + total + Double.parseDouble(travelDTO.toll)
                            + Double.parseDouble(travelDTO.parkingAmount) + Double.parseDouble(travelDTO.taxForReturn));

                    df.setRoundingMode(RoundingMode.UP);
                    travelDTO.totalAmount = df.format(resultAmount).toString();

                    Map pieceDetailsMap = VoucherUtil.mapDetail(travelDTO);

                    pieceFieldDetailsMaps.add(pieceDetailsMap);
                    String fileName = travelDTO.dateCreated.toString() + "-" + travelDTO.time.toString() + "-" + travelDTO.company + "-" + travelDTO.orderNumber;

                    try {

                        JasperReportUtil.generate(fileName,pieceFieldDetailsMaps,pieceDetailsMap);

                        File pdf = new File(VoucherUtil.path + fileName);

                        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdf));

                        byte[] bytes = Files.readAllBytes(pdf.toPath());

                        String b64 = Base64.getEncoder().encodeToString(bytes);

                        voucher.get().setVoucher(b64);
                        voucerRepo.save(voucher.get());

                        return ResponseEntity.ok()
                                // Content-Disposition
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pdf.getName())
                                // Content-Type
                                .contentType(MediaType.APPLICATION_PDF)
                                // Contet-Length
                                .contentLength(pdf.length()) //
                                .body(resource);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return completableFuture;
    }

    public CompletableFuture<Long> create(Travel travel) {

        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Voucher v = new Voucher();
                    v.setTravelId(travel.getOrderNumber());
                    v.setCompany(travel.getCompany());
                    v.setDateCreated(LocalDate.now());
                    v.setTime(LocalDateTime.now());
                    v.setStatus(VoucherStatus.CREATED);
                    voucerRepo.save(v);
                    return v.getTravelId();
                });
        return completableFuture;
    }

    public CompletableFuture<ResponseEntity> findAll(Pageable pageable) {

        CompletableFuture<ResponseEntity> completableFuture = CompletableFuture.supplyAsync(() -> {
            Page<Voucher> page = voucerRepo.findAll(pageable);
            return ResponseEntity.ok().body(page);

        });

        return completableFuture;
    }

    public CompletableFuture<Object> saveSignatureVoucher(SignatureVoucherDTO signatureVoucherDTO) {

        CompletableFuture<SignatureVoucherDTO> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    SignatureVoucher signatureV = new SignatureVoucher();
                    signatureV.setTravelId(signatureVoucherDTO.getTravelId());
                    signatureV.setBase64(signatureVoucherDTO.getBase64());
                    signatureV.setContentType(signatureVoucherDTO.getContentType());
                    iSignatureRepo.save(signatureV);
                    return signatureVoucherDTO;
                });

        CompletableFuture<Object> notified = completableFuture.thenApply(signature -> setSignatureIntoVoucher(signature));
        return notified;
    }

    public CompletableFuture<Object> setSignatureIntoVoucher(SignatureVoucherDTO signatureVoucherDTO) {

        CompletableFuture<Voucher> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Optional<Voucher> voucher = voucerRepo.findByTravelId(signatureVoucherDTO.getTravelId());
                    voucher.orElseThrow(RuntimeException::new);
                    voucher.get().setDocumentSigned(signatureVoucherDTO.getBase64());
                    voucher.get().setStatus(VoucherStatus.READY);
                    voucerRepo.save(voucher.get());
                    return voucher.get();
                }
        );
        CompletableFuture<Object> notified = completableFuture.thenApply(voucher -> sendPdfToPassenger(voucher));
        return notified;
    }

    public CompletableFuture<Object> sendPdfToPassenger (Voucher voucher) {

        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Travel travel = travelRepo.getTravel(voucher.getTravelId());
                    NotificationVoucherDTO notificationVoucherDTO = new NotificationVoucherDTO(voucher.getTravelId(), travel.getPassengerEmail());
                    notificationRepo.sendPdfToPassenger(notificationVoucherDTO);
                    return "PDF Enviado";
                }
        );
        return completableFuture;
    }

}
