package com.muling3.ecommerce.service;

import com.muling3.ecommerce.models.Customer;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailConfirmationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(Customer customer, String link) throws MessagingException, UnsupportedEncodingException {
        String recipient = customer.getEmail();
        String sender = "mulingealexmuli@gmail.com";
        String subject = "Email Verification from Spring Ecommerce. Please verify";
        String body = "Dear [[name]].<br/>"
                + "Please click the link below to verify your registration: <br/>"
                + "<h3><a href=\"[[link]]\" target=\"_self\">Confirm Email</a></h3>"
                + "Thank you for creating account with us</br>"
                + "Enjoy our services </br>"
                + "BY Alexander Muli";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        helper.setFrom(sender, "Spring Ecommerce");
        helper.setTo(recipient);
        helper.setSubject(subject);

        body = body.replace("[[name]]", customer.getUsername());
        body = body.replace("[[link]]", link);

        helper.setText(body, true);

        mailSender.send(mimeMessage);
    }
}
