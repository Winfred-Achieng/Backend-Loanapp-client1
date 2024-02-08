package com.winfred.loan_app.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class RegistrationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IdSequence")
//    @SequenceGenerator(name = "IdSequence", sequenceName = "IdSequence", allocationSize = 1)
    @Column(name="id")
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name="name")
    private String name;

    @NotBlank(message = "Phone is required")
    @Column(name="phone")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name="email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(name="password")
     String password;

    @Column(name="created_at")
    private Date created_at;

    @Transient
    private Date updated_at;

    @Column(name="otp")
    private String otp;

    @Column(name="otpStatus")
    private String otpStatus="pending";

    @Column(name ="lastOtpGeneratedTime")
    private LocalDateTime lastOtpGeneratedTime;



    @PrePersist
    protected void onCreate(){
        this.created_at = new Date();
    }
}
