package com.winfred.loan_app.controller;

import com.winfred.loan_app.model.RegistrationRequest;
import com.winfred.loan_app.model.VerificationRequest;
import com.winfred.loan_app.response.ApiResponse;
import com.winfred.loan_app.service.VerificationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class VerificationController {

    public VerificationService verificationService;
    public static final Logger log = LoggerFactory.getLogger(VerificationController.class);

    @PostMapping("/verifyOtp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody VerificationRequest verificationRequest) {

        RegistrationRequest registrationRequest = verificationService.getUserByEmail(verificationRequest.getEmail());

        if (registrationRequest != null) {
            String enteredOtp = verificationRequest.getEnteredOtp();
            boolean isOtpVerified = verificationService.verifyOtp(enteredOtp, registrationRequest);

            if (isOtpVerified) {
                registrationRequest.setOtpStatus("verified");

                log.info("Otp verified successfully for user: {}", registrationRequest.getEmail());
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Otp verified successfully", null));
            } else {
                log.warn("Incorrect OTP for user: {}", registrationRequest.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "Incorrect otp", null));
            }
        } else {
            log.warn("User not found for email: {}", verificationRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "User not found", null));
        }
    }




}