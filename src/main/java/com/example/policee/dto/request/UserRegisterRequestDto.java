package com.example.policee.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequestDto {
    String name;
    String lastname;
    Integer age;
    String username;
    String email;
    String password;
}
