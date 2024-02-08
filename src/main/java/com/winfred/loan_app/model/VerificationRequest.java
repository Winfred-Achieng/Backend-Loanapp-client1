package com.winfred.loan_app.model;

import lombok.Data;

@Data
public class VerificationRequest {
    private String enteredOtp;
    private String email;
}
