package com.muling3.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "email_confirmation")
@Data
@NoArgsConstructor
public class EmailConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String token;
    private Date expirationTime;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public EmailConfirmation(String token, Customer customer){
        this.token = token;
        this.customer = customer;
        this.expirationTime = calculateExpirationTime();
    }

    private Date calculateExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());

        //setting expiration time to 10 minutes from the current system time
        calendar.add(Calendar.MINUTE, 10);
        return new Date(calendar.getTime().getTime());
    }

}