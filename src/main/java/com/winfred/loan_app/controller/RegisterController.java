package com.winfred.loan_app.controller;

import com.winfred.loan_app.config.EmailConfiguration;
import com.winfred.loan_app.config.SmsHelper;
import com.winfred.loan_app.model.ResendOtpRequest;
import com.winfred.loan_app.model.SmsRequestModel;
import com.winfred.loan_app.model.UserResponse;
import com.winfred.loan_app.response.ApiResponse;
import com.winfred.loan_app.model.RegistrationRequest;
import com.winfred.loan_app.service.RegisterUserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class RegisterController {

    private final RegisterUserService registerUserService;
    private final EmailConfiguration emailConfiguration;
    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);
    @Autowired
    SmsHelper smsHelper;


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        RegistrationRequest registeredRegistrationRequest = registerUserService.registerUser(registrationRequest);

        if (registeredRegistrationRequest != null) {
            try {
                String otp = registerUserService.generateOtp(registrationRequest);
                registerUserService.sendEmail(registrationRequest.getEmail(), otp, emailConfiguration.getProperties());
//                registerUserService.sendSms(registrationRequest.getPhone(), otp);
                SmsRequestModel sms= SmsRequestModel.builder().message("Your OTP is "+otp).phoneNumber(registrationRequest.getPhone()).build();
                registrationRequest.setOtp(otp);
                String response=smsHelper.sendSms(sms);
                log.info("get error or success logs"+response);
                registerUserService.updateUserOtp(registrationRequest);


                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true,"Success. OTP sent to \" + registeredUser.getEmail() + \" and \" + registeredUser.getPhone()",null));
            } catch (Exception e) {
                e.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false,"Failed to send OTP",null));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false,"Failed to register",null));
        }
    }

    @PostMapping("/resendOtp")
    public ResponseEntity<ApiResponse<String>> resendOtp(@RequestBody ResendOtpRequest resendOtpRequest ) {
        try {
            String result = registerUserService.resendOtp(resendOtpRequest.getEmail());
            return ResponseEntity.ok(new ApiResponse<>(true, result, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/registeredUsers")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getRegisteredUsers() {
        try {
            List<RegistrationRequest> registeredUsers = registerUserService.getAllRegisteredUsers();

            List<UserResponse> userResponses = registeredUsers.stream()
                    .map(user -> new UserResponse(user.getName(), user.getPhone(), user.getEmail()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse<>(true, "Successfully retrieved registered users", userResponses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to retrieve registered users", null));
        }
    }


}
