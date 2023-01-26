package com.muling3.ecommerce.models.utils;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetRequest {
    private String userEmail;
    private String oldPassword;
    private String newPassword;
}