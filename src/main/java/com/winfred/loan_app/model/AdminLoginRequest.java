package com.winfred.loan_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
public class AdminLoginRequest {
    private String username;
    private String password;
}
