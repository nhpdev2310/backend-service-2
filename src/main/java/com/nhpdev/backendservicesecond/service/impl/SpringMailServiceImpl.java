package com.nhpdev.backendservicesecond.service.impl;

import com.nhpdev.backendservicesecond.service.MailService;
import com.nhpdev.backendservicesecond.service.TokenService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpringMailServiceImpl implements MailService {

    @Value( "${spring.mail.username}")
    private String from;

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Async
    @Override
    public void sendVerificationEmail(String to, String subject,
                                      String displayName,
                                      String templateName,
                                      String verificationLink) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(from, "TheBeastLibrary");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setSentDate(new Date());

            Context context = new Context();
            Map<String, Object> variables = Map.of(
                    "displayName", displayName,
                    "verificationUrl", verificationLink
            );
            context.setVariables(variables);
            String htmlContent = this.templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending email", e);
        }
    }


}
