package com.qnguyendev.backendservice.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j(topic = "EMAIL-SERVICE")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailService {

    SendGrid sendGrid;

    @Value("${spring.sendgrid.from-email}")
    @NonFinal
    String fromEmail;

    @Value("${spring.sendgrid.from-name}")
    @NonFinal
    String fromName;

    @Value("${spring.sendgrid.verification-template-id}")
    @NonFinal
    String verificationTemplateId;

    @Value("${spring.sendgrid.verification-link}")
    @NonFinal
    String verificationLink;

    /**
     * Send email by Sendhgrid
     * @param to send mail to someone
     * @param subject
     * @param body
     */
    public void send(String to, String subject, String body) {

        Email sender = new Email(fromEmail, fromName);
        Email recipient = new Email(to);

        Content content = new Content("text/plain", body);
        Mail mail = new Mail(sender, subject, recipient, content);

        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);
            if(response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
                log.info("Email sent successfully");
            } else {
                log.error("Failed to send email. Status code: {}", response.getStatusCode());
                log.error("Response body: {}", response.getBody());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Send verification email by Sendhgrid
     * @param to send mail to someone
     * @param name
     */
    public void emailVerification(String to, String name) throws IOException {
        log.info("Email verification started");

        Email sender = new Email(fromEmail, fromName);
        Email recipient = new Email(to);

        String subject = "Email verification";

        String secretCode = String.format("?secretCode=%s", UUID.randomUUID());

        // TODO generate secretCode and save to database

        Map<String, String> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("verificationLink", verificationLink + "/?secretCode=" + secretCode);

        Mail mail = new Mail();
        mail.setFrom(sender);
        mail.setSubject(subject);

        Personalization personalization = new Personalization();
        personalization.addTo(recipient);

        templateData.forEach(personalization::addDynamicTemplateData);

        mail.addPersonalization(personalization);
        mail.setTemplateId(verificationTemplateId);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sendGrid.api(request);
        if(response.getStatusCode() == HttpStatus.ACCEPTED.value()) {
            log.info("Verification sent successfully");
        } else {
            log.error("Failed to send verification. Status code: {}", response.getStatusCode());
            log.error("Response body: {}", response.getBody());
        }

    }
}
