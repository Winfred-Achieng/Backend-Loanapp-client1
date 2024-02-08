package com.winfred.loan_app.service;

import com.winfred.loan_app.model.RegistrationRequest;
import com.winfred.loan_app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VerificationService {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    @Transactional
    public boolean verifyOtp(String enteredOtp, RegistrationRequest registrationRequest){
        try {
            String generatedOtp = registrationRequest.getOtp();
            boolean isOtpVerified = enteredOtp.equals(generatedOtp);

            if (isOtpVerified) {
                registrationRequest.setOtpStatus("verified");
                registrationRequest.setLastOtpGeneratedTime(LocalDateTime.now());
                userRepository.updateOtpStatusAndLastOtpGeneratedTime
                        (registrationRequest.getId(), "verified", registrationRequest.getLastOtpGeneratedTime());
            }
            return isOtpVerified;
        }catch (Exception e) {
            log.error("Error during OTP verification", e);
            throw e;
        }
    }


    public RegistrationRequest getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
