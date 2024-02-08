package com.winfred.loan_app.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsRequestModel {

    String message;
    String phoneNumber;

}
