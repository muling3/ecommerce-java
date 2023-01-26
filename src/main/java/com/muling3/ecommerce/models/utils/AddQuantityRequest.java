package com.muling3.ecommerce.models.utils;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddQuantityRequest {
    private int quantity;
    private String productName;
    private String userName;
}
