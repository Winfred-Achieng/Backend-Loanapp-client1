package com.winfred.loan_app.repository;

import com.winfred.loan_app.model.RegistrationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<RegistrationRequest, Long> {

    Optional<RegistrationRequest> findByName(String name);
    RegistrationRequest findByEmail(String email);
    RegistrationRequest findByPhone(String phone);

    boolean existsByEmailOrPhone(String email, String phone);

    @Modifying
    @Query("UPDATE RegistrationRequest u SET u.otpStatus = :otpStatus, u.lastOtpGeneratedTime = :lastOtpGeneratedTime WHERE u.id = :id")
    int updateOtpStatusAndLastOtpGeneratedTime(
            @Param("id") Long id,
            @Param("otpStatus") String otpStatus,
            @Param("lastOtpGeneratedTime") LocalDateTime lastOtpGeneratedTime
    );

    RegistrationRequest findByEmailAndPassword(String email, String password);

    RegistrationRequest findByPhoneAndPassword(String phone, String password);
}
