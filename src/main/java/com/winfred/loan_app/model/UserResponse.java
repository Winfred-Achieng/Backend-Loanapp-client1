package com.winfred.loan_app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String Name;
    private String phone;
    private String email;

}
