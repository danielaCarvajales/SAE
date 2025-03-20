package com.siscem.portal_sae.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginRequestDTO {
    private String email;
    private String password;
}
