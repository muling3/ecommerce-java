package com.muling3.ecommerce.models.utils;

import lombok.*;
import com.muling3.ecommerce.models.Cart;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCartRequest {
    private String username;
    private Cart cart;
}