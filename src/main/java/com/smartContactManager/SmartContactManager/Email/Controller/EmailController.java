package com.smartContactManager.SmartContactManager.Email.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartContactManager.SmartContactManager.Email.Services.EmailRequest;
import com.smartContactManager.SmartContactManager.Email.Services.EmailService;


@Controller
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Show the email form when accessed via GET
    @RequestMapping("/send")
    public String showEmailForm(Model model) {
        model.addAttribute("emailRequest", new EmailRequest()); // Prepare an empty emailRequest for the form
        return "send-email"; // Thymeleaf template (send-email.html)
    }

    // Handle the form submission via POST
    @PostMapping("/send")
    public String sendEmail(@RequestParam String to, @RequestParam String subject, @RequestParam String body, Model model) {
        // Create an EmailRequest object to hold form data
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setTo(to);
        emailRequest.setSubject(subject);
        emailRequest.setBody(body);

        // Call the EmailService to send the email
        String result = emailService.sendEmail(to, subject, body);

        // Add result to the model to display feedback
        if (result.equals("Email sent successfully")) {
            model.addAttribute("message", result);
        } else {
            model.addAttribute("error", result);
        }

        // Add emailRequest back to the model so that the form can be repopulated
        model.addAttribute("emailRequest", emailRequest);
        
        return "send-email"; // Return to the Thymeleaf template
    }
}

