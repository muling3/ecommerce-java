package com.muling3.ecommerce.models.utils;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RemoveCartProductRequest {
    private String username;
    private String productName;
}