package com.siscem.portal_sae.dtos.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor

public class EmailSendDTO {
    private String to;
    private String subject;
    private String body;
    private String email;
    private String password;
    private List<MultipartFile> attachments;
}
