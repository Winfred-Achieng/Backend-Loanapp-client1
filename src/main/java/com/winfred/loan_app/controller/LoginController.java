package com.winfred.loan_app.controller;

import com.winfred.loan_app.model.LoginRequest;
import com.winfred.loan_app.response.ApiResponse;
import com.winfred.loan_app.service.LoginUserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Date;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class LoginController {

    private final LoginUserService loginUserService;
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String SECRET_KEY ;
    static {
       SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            if (StringUtils.isEmpty(loginRequest.getUsername()) || StringUtils.isEmpty(loginRequest.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Invalid request. Username and password are required.", null));
            }

            log.debug("Received Login Request: {}", loginRequest);

            String loggedUser = loginUserService.loginUser(loginRequest.getUsername(), loginRequest.getPassword());
            String userName = loginUserService.getUserNameByEmailOrPhone(loginRequest.getUsername());

            if (userName != null) {
                String token = generateJwtToken(loggedUser);
                return ResponseEntity.status(200)
                        .body(new ApiResponse<>(true, "Success",userName));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Invalid credentials", null));
            }
        } catch (Exception e) {
            log.error("Exception during login:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Error occurred: " + e.getMessage(), null));
        }

    }


    private String generateJwtToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + 864_000_000))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

}
