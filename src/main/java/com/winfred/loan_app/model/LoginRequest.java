package com.winfred.loan_app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Transient;

@Data
public class LoginRequest {
    @JsonProperty("email")
    private String email;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("password")
    private String password;

    @Transient
    public String getUsername() {
        return email != null ? email : phone;
    }
}
