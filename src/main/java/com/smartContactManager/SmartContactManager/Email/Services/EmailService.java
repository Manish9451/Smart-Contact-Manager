package com.smartContactManager.SmartContactManager.Email.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@ComponentScan 
@Service
public
class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public String sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
            return "Email sent successfully";
        } catch (Exception e) {
            return "Error while sending email: " + e.getMessage();
        }
    }
}

