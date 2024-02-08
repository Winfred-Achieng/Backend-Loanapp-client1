package com.winfred.loan_app.controller;


import com.winfred.loan_app.model.AdminLoginRequest;
import com.winfred.loan_app.response.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class AdminController {

    private static final String ADMIN_USERNAME ="admin";
    private static final String ADMIN_PASSWORD = "admin123";


    @PostMapping("/adminLogin")
    public ResponseEntity<ApiResponse<String>> adminLogin(@RequestBody AdminLoginRequest adminLoginRequest){

        if(ADMIN_USERNAME.equals(adminLoginRequest.getUsername()) && ADMIN_PASSWORD.equals(adminLoginRequest.getPassword())){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Admin login successful",null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false,"Invalid admin credentials",null));
    }
}
