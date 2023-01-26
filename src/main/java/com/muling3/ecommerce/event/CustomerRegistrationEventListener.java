package com.muling3.ecommerce.event;

import com.muling3.ecommerce.models.Customer;
import com.muling3.ecommerce.service.CustomerService;
import com.muling3.ecommerce.service.EmailConfirmationService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@Component
public class CustomerRegistrationEventListener implements ApplicationListener<CustomerRegistrationEvent> {
    @Autowired
    private CustomerService customerService;
    @Autowired private EmailConfirmationService emailService;

    @Override
    public void onApplicationEvent(CustomerRegistrationEvent event) {
        Customer customer = event.getCustomer();
        String token = UUID.randomUUID().toString();

        customerService.saveCustomerConfirmationToken(customer, token);

        //sending the email confirmation link
        String link = event.getApplicationUrl() + "/confirmEmail?token="+token;
        try {
            emailService.sendVerificationEmail(customer, link);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        log.info("Confirmation link is: {}",link);
    }
}
