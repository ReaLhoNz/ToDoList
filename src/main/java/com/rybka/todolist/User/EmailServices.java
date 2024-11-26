package com.rybka.todolist.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
public class EmailServices {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;

    // Constructor to inject dependencies
    public EmailServices(TemplateEngine templateEngine, JavaMailSender emailSender) {
        this.templateEngine = templateEngine;
        this.emailSender = emailSender;
    }

    // Send email using a specific email context and Thymeleaf template
    public void sendMail(AccountVerificationEmailContext email) throws MessagingException {
        // Create the MimeMessage
        MimeMessage message = emailSender.createMimeMessage();

        // Helper to manage email content (multipart to support HTML emails)
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        // Set up the Thymeleaf context with variables from the email context
        Context context = new Context();
        context.setVariables(email.getContext());  // Assuming getContext() returns a Map<String, Object> for the template

        // Render the HTML email content using Thymeleaf template
        String emailContent = templateEngine.process("emailtempalte", context);  // 'emailtempalte' is the template name, without the '.html' extension

        // Set up the recipient, subject, and from address
        mimeMessageHelper.setTo(email.getTo());
        mimeMessageHelper.setSubject(email.getSubject());
        mimeMessageHelper.setFrom(email.getFrom());

        // Set the HTML content in the email
        mimeMessageHelper.setText(emailContent, true);  // 'true' indicates that it's an HTML email

        // Send the email
        emailSender.send(message);
    }
}
