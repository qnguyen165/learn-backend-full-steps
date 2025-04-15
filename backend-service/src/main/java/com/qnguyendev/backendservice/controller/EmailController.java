package com.qnguyendev.backendservice.controller;

import com.qnguyendev.backendservice.service.EmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Tag(name = "Email Controller")
@Slf4j(topic = "EMAIL-CONTROLLER")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {

    EmailService emailService;

    @GetMapping("/send-email")
    public void sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        log.info("Send email to: {}", to);
        emailService.send(to, subject, body);
        log.info("Email sent successfully");
    }

    @GetMapping("/verify-email")
    public void verifyEmail(@RequestParam String to, @RequestParam String name) {
        log.info("Verifying email to: {}", to);
        try {
            emailService.emailVerification(to, name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Verified successfully");
    }
}
