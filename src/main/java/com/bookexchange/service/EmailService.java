package com.bookexchange.service;

import com.bookexchange.constant.AuthenticationAction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {


    @NonFinal
    @Value("${brevo.api-key}")
    protected String apiKey;

    @NonFinal
    @Value("${brevo.email}")
    protected String senderEmail;

    @NonFinal
    @Value("${brevo.name}")
    protected String senderName;

    @NonFinal
    @Value("${brevo.verify-email-url}")
    protected String verifyEmailUrl;

    public void sendVerificationEmailApi(String to, String subject, String token) throws ApiException {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName(senderName);

        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        recipient.setEmail(to);

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(sender);
        sendSmtpEmail.setTo(List.of(recipient));
        sendSmtpEmail.setSubject(subject);

        String htmlContent =
                "<html>" +
                "<body>" +
                "<h2>Xác thực tài khoản của bạn</h2>" +
                "<p>Vui lòng nhấp vào liên kết bên dưới để xác thực tài khoản:</p>" +
                "<a href='" + verifyEmailUrl + token + "'>Xác thực tài khoản</a>" +
                "<p>Liên kết này sẽ hết hạn sau 24 giờ.</p>" +
                "</body>" +
                "</html>";

        sendSmtpEmail.setHtmlContent(htmlContent);

        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            log.error("Error sending email: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void sendForgotPasswordEmailApi(String to, String subject, String token, String temporaryPw) throws ApiException {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(apiKey);

        TransactionalEmailsApi apiInstance = new TransactionalEmailsApi();

        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(senderEmail);
        sender.setName(senderName);

        SendSmtpEmailTo recipient = new SendSmtpEmailTo();
        recipient.setEmail(to);

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();
        sendSmtpEmail.setSender(sender);
        sendSmtpEmail.setTo(List.of(recipient));
        sendSmtpEmail.setSubject(subject);

        String htmlContent =
                "<html>" +
                "<body>" +
                "<h2>Xác thực tài khoản của bạn</h2>" +
                "<p>Mật khẩu tạm thời:</p>" + temporaryPw +
                "<p>Vui lòng nhấp vào liên kết bên dưới để xác thực tài khoản:</p>" +
                "<a href='" + verifyEmailUrl + token + "&action=" + AuthenticationAction.FORGOT_PASSWORD + "'>Xác thực tài khoản</a>" +
                "<p>Chú ý: Nếu bạn không xác minh tài khoản, bạn không thể đăng nhập vào hệ thống</p>" +
                "<p>Liên kết này sẽ hết hạn sau 24 giờ.</p>" +
                "</body>" +
                "</html>";

        sendSmtpEmail.setHtmlContent(htmlContent);

        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
        } catch (ApiException e) {
            log.error("Error sending email: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}