package com.winfred.loan_app.config;

import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.Properties;

@Configuration
public class EmailConfiguration {

    private static final String PROPERTIES_FILE = "email.properties";

    private Properties properties;

    public EmailConfiguration() {
        loadProperties();
    }

    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + PROPERTIES_FILE);
                return;
            }
            properties.load(input);

            // Use a default value if smtpPort is not specified in the properties file
            if (properties.getProperty("smtpPort") == null) {
                properties.setProperty("smtpPort", "587");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSmtpHost() {
        return properties.getProperty("smtpHost");
    }

    public String getSmtpUsername() {
        return properties.getProperty("smtpUsername");
    }

    public String getSmtpPassword() {
        return properties.getProperty("smtpPassword");
    }

    public int getSmtpPort() {
        return Integer.parseInt(properties.getProperty("smtpPort"));
    }

    public Properties getProperties() {
        return properties;
    }
}
