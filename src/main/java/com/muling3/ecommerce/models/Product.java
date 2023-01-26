package com.muling3.ecommerce.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id_sequence"
    )
    @SequenceGenerator(
            name = "product_id_sequence",
            sequenceName = "product_id_sequence",
            initialValue = 1,
            allocationSize = 1
    )
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "description")
    private String description;
}
