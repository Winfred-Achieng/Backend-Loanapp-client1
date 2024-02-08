package com.winfred.loan_app.controller;

import com.winfred.loan_app.response.ApiResponse;
import com.winfred.loan_app.service.LoginUserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final LoginUserService loginUserService;
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);



    @GetMapping("/getUsername")
    public ResponseEntity<ApiResponse<String>> getUsername(@RequestParam String email) {
        try {
            String username = loginUserService.getUsernameByEmail(email);

            if (username != null) {
                return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Success", username));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Username not found for email", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Error occurred", null));
        }
    }
}