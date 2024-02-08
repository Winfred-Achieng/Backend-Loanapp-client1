package com.winfred.loan_app.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("africastalking")
@Data
public class AfricaTalkingProperties {
    private String username;
    private String apiKey;

}
