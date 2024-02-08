package com.winfred.loan_app.service;


import com.winfred.loan_app.config.AfricaTalkingProperties;
import com.winfred.loan_app.config.EmailConfiguration;
import com.winfred.loan_app.config.SmsHelper;
import com.winfred.loan_app.exception.UserNotFoundException;
import com.winfred.loan_app.model.RegistrationRequest;
import com.winfred.loan_app.model.SmsRequestModel;
import com.winfred.loan_app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.ConstraintViolation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class RegisterUserService {

    private final UserRepository userRepository;
    private final Validator validator;
    private static final Logger log = LoggerFactory.getLogger(RegisterUserService.class);
    private final EmailConfiguration emailConfiguration;

    private AfricaTalkingProperties africaTalkingProperties;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    SmsHelper smsHelper;

    public RegistrationRequest registerUser(RegistrationRequest registrationRequest) {
        try {
//            Set<ConstraintViolation<RegistrationRequest>> violations = validator.validate(registrationRequest);
//            if (!violations.isEmpty()) {
//                throw new ConstraintViolationException(violations);}

            if (userRepository.existsByEmailOrPhone(registrationRequest.getEmail(), registrationRequest.getPhone())) {
                return null;}

            String hashedPassword = passwordEncoder.encode(registrationRequest.getPassword());
            registrationRequest.setPassword(hashedPassword);
//            registrationRequest.setPassword(registrationRequest.getPassword());

            RegistrationRequest savedRegistrationRequest = userRepository.save(registrationRequest);
            return userRepository.findById(savedRegistrationRequest.getId()).orElse(null);
        }catch (DataIntegrityViolationException e){
            log.error("User registration failed: {}",e.getMessage(),e);
            throw new RuntimeException("User registration failed. Email or phone already exists.");
        }catch (Exception e){
            log.error("User registration failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void updateUserOtp(RegistrationRequest registrationRequest){
        userRepository.updateOtpStatusAndLastOtpGeneratedTime(
                registrationRequest.getId(),
                registrationRequest.getOtpStatus(),
                registrationRequest.getLastOtpGeneratedTime()
        );
        userRepository.save(registrationRequest);
    }


    public String generateOtp(RegistrationRequest registrationRequest){
        registrationRequest.setOtp(generateNewOtp());
        registrationRequest.setLastOtpGeneratedTime(LocalDateTime.now());
        return  registrationRequest.getOtp();
    }
    public String generateNewOtp() {
        //should encode OTP
//        String otp  =String.format("%06d", new Random().nextInt(999999));
//        return passwordEncoder.encode(otp);
        return String.format("%06d", new Random().nextInt(999999));
    }


    public String resendOtp(String email) {
        RegistrationRequest registrationRequest = userRepository.findByEmail(email);
        if (registrationRequest.getEmail() == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

        if ("verified".equals(registrationRequest.getOtpStatus())) {
            throw new RuntimeException("User is already verified");
        }

        String newOtp = generateOtp(registrationRequest);
        sendEmail(registrationRequest.getEmail(), newOtp, emailConfiguration.getProperties());
//        sendSms(registrationRequest.getPhone(), newOtp);
        SmsRequestModel sms= SmsRequestModel.builder().message("Your OTP is "+newOtp).phoneNumber(registrationRequest.getPhone()).build();
        String response=smsHelper.sendSms(sms);
        updateUserOtp(registrationRequest);
        return "OTP resent successfully";

    }



    public void sendEmail(String email, String otp, Properties emailProperties) {

        String smtpHost = emailProperties.getProperty("mail.smtp.host");
        String smtpUsername = emailProperties.getProperty("mail.smtp.username");
        String smtpPassword = emailProperties.getProperty("mail.smtp.password");
        String smtpSender = emailProperties.getProperty("email.sender");
        int smtpPort = Integer.parseInt(emailProperties.getProperty("mail.smtp.port"));

        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host",smtpHost);
        props.put("mail.smtp.port",smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUsername,smtpPassword);
            }
        });
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(smtpSender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Your OTP for Registration");
            message.setText("Your OTP is: " + otp);
            Transport.send(message);
            System.out.println("Email sent successfully");

        }  catch (MessagingException e) {
            e.printStackTrace();
        }
    }

//    public void sendSms(String toPhoneNumber, String otp) {
//        log.info("Using username: {}", africaTalkingProperties.getUsername());
//        log.info("Using API key: {}", africaTalkingProperties.getApiKey());
//        try {
//            String formattedPhoneNumber = validateAndFormatPhoneNumber(toPhoneNumber);
//
//            AfricasTalking.initialize(africaTalkingProperties.getUsername(), africaTalkingProperties.getApiKey());
//
//            SmsService smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
//            List<Recipient> response = smsService.send("Your OTP is: " + otp, new String[]{formattedPhoneNumber}, true);
//
//            log.info("SMS response: {}", response);
//            System.out.println("SMS sent successfully");
//        } catch (Exception e) {
//            log.error("SMS sending failed: {}", e.getMessage(), e);
//            throw new RuntimeException("SMS sending failed");
//        }
//    }

    private String validateAndFormatPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\s", "");
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+254" + phoneNumber;
        }
        return phoneNumber;
    }


    public List<RegistrationRequest> getAllRegisteredUsers(){
        return userRepository.findAll();
    }


}
