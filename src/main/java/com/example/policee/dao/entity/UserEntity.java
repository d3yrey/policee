package com.example.policee.dao.entity;

import com.example.policee.util.config.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)

public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String lastname;
    Integer age;
    String username;
    String email;
    String password;
    @CreationTimestamp
    LocalDateTime createAt;
    @UpdateTimestamp
    LocalDateTime updateAt;


    String otpCode;
    Integer verifyCount;
    @Enumerated(EnumType.STRING)
    Status status;
    LocalDateTime blocktime;
    LocalDateTime logintime;
    LocalDateTime otpExpireTime;



}
