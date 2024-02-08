package com.winfred.loan_app.service;

import com.winfred.loan_app.exception.AuthenticationFailedException;
import com.winfred.loan_app.exception.UserNotFoundException;
import com.winfred.loan_app.model.RegistrationRequest;
import com.winfred.loan_app.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class LoginUserService {

    private static final long EXPIRATION_TIME =864_000_000 ;//10 days
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded());
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(LoginUserService.class);



    public String loginUser(String username, String password) {
        try {
            RegistrationRequest user;
            if (isEmail(username)) {
                user = userRepository.findByEmail(username);
            } else {
                user = userRepository.findByPhone(username);
            }
            if (user != null && verifyPassword(password, user.getPassword())) {
                log.debug("User authenticated: {}", user);
                String token = generateJwtToken(user);
                return token;
            } else {
                log.debug("Authentication failed for user: {}", username);
                throw new AuthenticationFailedException("Authentication failed");
            }
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage(), e);
            throw new AuthenticationFailedException("Authentication failed", e);
        }
    }


    private boolean verifyPassword(String password, String storedPassword) {
        String trimmedPassword = password.trim();
        boolean passwordMatches= passwordEncoder.matches(trimmedPassword, storedPassword);
//        boolean passwordMatches = password.equals(storedPassword);
        if (!passwordMatches){
            log.debug("Password verification failed for user. Entered Password: {}, Stored Password: {}", password, storedPassword);
        }
        return passwordMatches;
    }
    private boolean isEmail(String input) {
        return input!=null && input.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$");
    }


    public String generateJwtToken(RegistrationRequest registrationRequest) {
        return Jwts.builder()
                .setSubject(registrationRequest.getName())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .claim("username", registrationRequest.getName())
                .signWith(SignatureAlgorithm.HS512,SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            String username = (String) claims.get("username");
            log.debug("Retrieved username from token: {}", username);
            return username;
        } catch (Exception e) {
            log.error("Error extracting username from token: {}", e.getMessage(), e);
            return null;
        }
    }

    public String getUsernameByEmail(String email) {
        RegistrationRequest user = userRepository.findByEmail(email);
        return user.getName();
    }

    public String getUserNameByEmailOrPhone(String username) {
        RegistrationRequest user;

        if (isEmail(username)) {
            user = userRepository.findByEmail(username);
        } else {
            user = userRepository.findByPhone(username);
        }
        if (user != null) {
            return user.getName();
        } else {
            throw new UserNotFoundException("User not found for the given credentials");

        }
    }
}

