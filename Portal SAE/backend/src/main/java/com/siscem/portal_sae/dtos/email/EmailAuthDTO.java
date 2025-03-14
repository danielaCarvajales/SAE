package com.siscem.portal_sae.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailAuthDTO {
    private String email;
    private String contrasena;

}
