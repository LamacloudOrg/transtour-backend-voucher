package com.transtour.backend.voucher.service;


import com.github.dozermapper.core.Mapper;
import com.transtour.backend.voucher.dto.SignatureVoucherDTO;
import com.transtour.backend.voucher.dto.TravelDTO;
import com.transtour.backend.voucher.excption.VoucherNotReady;
import com.transtour.backend.voucher.model.SignatureVoucher;
import com.transtour.backend.voucher.model.Travel;
import com.transtour.backend.voucher.model.Voucher;
import com.transtour.backend.voucher.model.VoucherStatus;
import com.transtour.backend.voucher.repository.ISignatureVoucherRepository;
import com.transtour.backend.voucher.repository.ITravelRepo;
import com.transtour.backend.voucher.repository.IVoucherRepository;
import com.transtour.backend.voucher.util.VoucherUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
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
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Decoder;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
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

    @Autowired
    Mapper mapper;


    public CompletableFuture<ResponseEntity> exportVoucher(String voucherId) throws FileNotFoundException, JRException {
        //TODO agregar regla que solo se permita genera el voucher si esta en estado ready

        CompletableFuture<ResponseEntity> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    ArrayList<Map<String, Object>> pieceFieldDetailsMaps = new ArrayList<Map<String, Object>>();

                    Optional<Voucher> voucher = voucerRepo.findByTravelId(voucherId);

                    voucher.orElseThrow(RuntimeException::new);

                    if (!voucher.get().getStatus().equals(VoucherStatus.READY)) throw new VoucherNotReady();

                    Travel travel = travelRepo.getTravel(voucher.get().getTravelId());

                    TravelDTO travelDTO = new TravelDTO();

                    mapper.map(travel, travelDTO);

                    String document = voucher.get().getDocumentSigned();

                    BufferedImage image = null;
                    byte[] imageByte;
                    String signatureFile =VoucherUtil.path + travel.getOrderNumber() + "-" + LocalDateTime.now().toString();

                    try {
                        BASE64Decoder decoder = new BASE64Decoder();
                        imageByte = decoder.decodeBuffer(document);
                        ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
                        image = ImageIO.read(bis);

                        ImageIO.write(image, "jpg", new File(signatureFile));

                        bis.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e.getLocalizedMessage());
                    }

                    LOG.debug("file "+signatureFile + "exist "+ FileUtils.getFile(signatureFile).exists());

                    travelDTO.setSignature(signatureFile);

                    //TODO saca esto de aca
                     Double resultAmout = ( Double.parseDouble(travelDTO.amount) + Double.parseDouble(travelDTO.whitingTime) + Double.parseDouble(travelDTO.toll)
                            + Double.parseDouble(travelDTO.parkingAmount) + Double.parseDouble(travelDTO.taxForReturn) );
                    travelDTO.totalAmount = Double.toString(resultAmout);
                    //TODO saca esto de aca

                    Map pieceDetailsMap = VoucherUtil.mapDetail(travelDTO);

                    pieceFieldDetailsMaps.add(pieceDetailsMap);
                    String fileName = travelDTO.dateCreated.toString() + "-" + travelDTO.time.toString() + "-" + travelDTO.company + "-" + travelDTO.orderNumber;

                    try {
                        File jasper = ResourceUtils.getFile(VoucherUtil.jasperFile);

                        JasperReport jasperReport = JasperCompileManager.compileReport(jasper.getAbsolutePath());
                        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pieceFieldDetailsMaps);

                        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, pieceDetailsMap, dataSource);
                        JasperExportManager.exportReportToPdfFile(jasperPrint, VoucherUtil.path + fileName);

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

    public CompletableFuture<String> create(Travel travel) {

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Voucher v = new Voucher();
                    v.setTravelId(travel.getOrderNumber());
                    v.setCompany(travel.getCompany());
                    v.setDateCreated(LocalDate.now());
                    v.setTime(LocalDateTime.now());
                    v.setStatus(VoucherStatus.CREATED);
                    voucerRepo.save(v);
                    return v.getId();
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

        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    Optional<Voucher> voucher = voucerRepo.findByTravelId(signatureVoucherDTO.getTravelId());
                    voucher.orElseThrow(RuntimeException::new);
                    voucher.get().setDocumentSigned(signatureVoucherDTO.getBase64());
                    voucher.get().setStatus(VoucherStatus.READY);
                    voucerRepo.save(voucher.get());
                    return "Voucher created";
                }
        );
        return completableFuture;
    }
}
