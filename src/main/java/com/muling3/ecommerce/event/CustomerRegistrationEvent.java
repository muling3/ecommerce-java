package com.muling3.ecommerce.event;

import com.muling3.ecommerce.models.Customer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class CustomerRegistrationEvent extends ApplicationEvent {

    private Customer customer;
    private String applicationUrl;

    public CustomerRegistrationEvent(Customer source, String applicationUrl) {
        super(source);
        this.customer = source;
        this.applicationUrl = applicationUrl;
    }
}