package com.muling3.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long productId;
    private String productName;
    private int productQuantity;

    @Column(nullable = false)
    private double productPrice;
    private String productImageUrl;
    private boolean delivered = false;
    private String productDesc;

}