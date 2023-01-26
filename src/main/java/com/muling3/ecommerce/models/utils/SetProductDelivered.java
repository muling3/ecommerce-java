package com.muling3.ecommerce.models.utils;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetProductDelivered {
    private String username;
    private Long productId;
}