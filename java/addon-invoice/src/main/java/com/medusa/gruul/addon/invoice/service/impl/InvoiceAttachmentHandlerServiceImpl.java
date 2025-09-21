package com.medusa.gruul.addon.invoice.service.impl;

import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.invoice.addon.InvoiceAddonSupporter;
import com.medusa.gruul.addon.invoice.model.dto.InvoiceAttachmentDTO;
import com.medusa.gruul.addon.invoice.model.dto.ReSendInvoiceAttachmentDTO;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceError;
import com.medusa.gruul.addon.invoice.model.enums.InvoiceStatus;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceAttachment;
import com.medusa.gruul.addon.invoice.mp.entity.InvoiceRequest;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceAttachmentService;
import com.medusa.gruul.addon.invoice.mp.service.IInvoiceRequestService;
import com.medusa.gruul.addon.invoice.service.InvoiceAttachmentHandlerService;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceAttachmentHandlerServiceImpl implements InvoiceAttachmentHandlerService {

    private final IInvoiceRequestService invoiceRequestService;

    private final IInvoiceAttachmentService invoiceAttachmentService;
    private final InvoiceAddonSupporter invoiceAddonSupporter;
    private final JavaMailSender javaMailSender;
    private final Executor globalExecutor;

    @Value("${spring.mail.username:qisange2020@163.com}")
    private String account;

    /**
     * 上传发票附件
     *
     * @param invoiceAttachmentDTO 发票附件参数
     */
    @Override
    public void uploadInvoiceAttachment(InvoiceAttachmentDTO invoiceAttachmentDTO) {
        Long shopId = invoiceAttachmentDTO.getShopId();
        InvoiceRequest invoiceRequest = invoiceRequestService.lambdaQuery()
                .select(InvoiceRequest::getHeader, InvoiceRequest::getEmail, InvoiceRequest::getOrderNo)
                .eq(InvoiceRequest::getId, invoiceAttachmentDTO.getInvoiceRequestId())
                .eq(InvoiceRequest::getShopId, shopId)
                .one();
        if (invoiceRequest == null) {
            throw InvoiceError.INVOICE_DOES_NOT_EXIST.exception();
        }
        try {
            uploadInvoiceAttachment(invoiceAttachmentDTO.getInvoiceAttachmentUrl(), invoiceRequest.getHeader(),
                    invoiceRequest.getEmail());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new GlobalException("邮件发送失败", e);
        }
        boolean exists = invoiceAttachmentService.lambdaQuery()
                .eq(InvoiceAttachment::getInvoiceRequestId, invoiceAttachmentDTO.getInvoiceRequestId())
                .eq(InvoiceAttachment::getShopId, shopId)
                .exists();
        if (!exists) {
            invoiceAttachmentService.save(
                    new InvoiceAttachment()
                            .setInvoiceAttachmentUrl(invoiceAttachmentDTO.getInvoiceAttachmentUrl())
                            .setInvoiceRequestId(invoiceAttachmentDTO.getInvoiceRequestId())
                            .setShopId(shopId)
            );
            invoiceRequestService.lambdaUpdate()
                    //开票时间
                    .set(InvoiceRequest::getInvoiceTime, LocalDateTime.now())
                    .set(InvoiceRequest::getUpdateTime, LocalDateTime.now())
                    .set(InvoiceRequest::getInvoiceStatus, InvoiceStatus.SUCCESSFULLY_INVOICED)
                    .eq(InvoiceRequest::getId, invoiceAttachmentDTO.getInvoiceRequestId())
                    .eq(InvoiceRequest::getShopId, shopId)
                    .update();
            // 同步发票状态
            invoiceAddonSupporter.syncSupplyOrderInvoiceStatus(invoiceRequest.getOrderNo(),
                    InvoiceStatus.SUCCESSFULLY_INVOICED.getValue());
            return;
        }
        boolean update = invoiceAttachmentService.lambdaUpdate()
                .set(InvoiceAttachment::getInvoiceAttachmentUrl,
                        JSON.toJSONString(invoiceAttachmentDTO.getInvoiceAttachmentUrl()))
                .eq(InvoiceAttachment::getInvoiceRequestId, invoiceAttachmentDTO.getInvoiceRequestId())
                .eq(InvoiceAttachment::getShopId, invoiceAttachmentDTO.getShopId())
                .update();
        InvoiceError.INVOICE_ATTACHMENT_SAVED_FAILED.falseThrow(update);
    }

    /**
     * 下载发票附件
     *
     * @param invoiceAttachmentDTO 发票附件参数
     * @param response             响应
     */
    @Override
    public void downloadInvoiceAttachment(InvoiceAttachmentDTO invoiceAttachmentDTO, HttpServletResponse response) {
        InvoiceAttachment invoiceAttachment = invoiceAttachmentService.lambdaQuery()
                .eq(InvoiceAttachment::getInvoiceRequestId, invoiceAttachmentDTO.getInvoiceRequestId())
                .eq(InvoiceAttachment::getShopId, invoiceAttachmentDTO.getShopId())
                .one();
        if (invoiceAttachment == null) {
            return;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(byteArrayOutputStream);

            byte[] buffer = new byte[1024];
            //String[] fileUrls = invoiceAttachment.getInvoiceAttachmentUrl().split(",");
            for (String fileUrl : invoiceAttachment.getInvoiceAttachmentUrl()) {
                URL url = new URL(fileUrl);
                InputStream is = url.openStream();
                zos.putNextEntry(new ZipEntry(getFileNameFromUrl(fileUrl)));

                int length;
                while ((length = is.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                is.close();
                zos.closeEntry();
            }
            zos.close();

            response.setHeader("Content-Disposition", "attachment; filename=downloaded_files.zip");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.getOutputStream().write(byteArrayOutputStream.toByteArray());
            response.flushBuffer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 重新发送发票附件到邮箱
     *
     * @param dto {@link ReSendInvoiceAttachmentDTO}
     */
    @Override
    public void reSendAttachment(ReSendInvoiceAttachmentDTO dto) {
        // 检查发票申请状态
        InvoiceRequest invoiceRequest = invoiceRequestService
                .lambdaQuery()
                .eq(InvoiceRequest::getId, dto.getInvoiceRequestId())
                .eq(InvoiceRequest::getInvoiceStatus, InvoiceStatus.SUCCESSFULLY_INVOICED)
                .one();
        InvoiceError.INVOICE_REQUEST_NOT_EXIST.trueThrow(invoiceRequest == null);

        // 检查是否发送过附件
        InvoiceAttachment invoiceAttachment = invoiceAttachmentService
                .lambdaQuery()
                .eq(InvoiceAttachment::getInvoiceRequestId, dto.getInvoiceRequestId())
                .eq(InvoiceAttachment::getShopId, dto.getShopId())
                .one();
        InvoiceError.INVOICE_REQUEST_ATTACHMENT_NOT_EXIST.trueThrow(invoiceAttachment == null ||
                CollectionUtils.isEmpty(invoiceAttachment.getInvoiceAttachmentUrl()));

        // 发送附件
        CompletableTask.allOf(globalExecutor, () -> {
            try {
                uploadInvoiceAttachment(invoiceAttachment.getInvoiceAttachmentUrl(), invoiceRequest.getHeader(),
                        invoiceRequest.getEmail());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    private String getFileNameFromUrl(String url) {
        int lastIndexOfSlash = url.lastIndexOf('/');
        return url.substring(lastIndexOfSlash + 1);
    }


    private void uploadInvoiceAttachment(List<String> invoiceAttachmentUrls, String header, String email)
            throws Exception {
        List<Path> downloadedFiles = new ArrayList<>(invoiceAttachmentUrls.size());
        for (String invoiceAttachmentUrl : invoiceAttachmentUrls) {
            URL url = new URL(invoiceAttachmentUrl);
            String strUrl = url.toString();
            String prefix = strUrl.substring(strUrl.lastIndexOf("/") + 1, strUrl.lastIndexOf("."));
            String suffix = strUrl.substring(strUrl.lastIndexOf("."));
            Path tempFilePath = Files.createTempFile(prefix, suffix);
            Files.copy(url.openStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);
            downloadedFiles.add(tempFilePath);
        }
        sendEmailWithAttachments(downloadedFiles, header, email);
    }

    private void sendEmailWithAttachments(List<Path> files, String header, String email)
            throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(account);
        helper.setTo(email);
        helper.setSubject(header);
        helper.setText("请下载附件");
        for (Path file : files) {
            byte[] fileContent = Files.readAllBytes(file);
            helper.addAttachment(file.getFileName().toString(), new ByteArrayResource(fileContent));
        }
        javaMailSender.send(message);
    }

}
