package com.example.policee.rest;

import com.example.police.dto.request.*;
import com.example.police.dto.response.TokenResponseDto;
import com.example.police.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;


    @PostMapping("/register")
    public String register(@RequestBody UserRegisterRequestDto dto){
        authService.saveUser(dto);
        return "mailinize otp kod gonderildi ";
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerifyRequestDto dto) {
        authService.verifyOtp(dto);
        return ResponseEntity.ok("Qeydiyyat tamamlandı");
    }


    @PostMapping("/resend-otp")
    public ResponseEntity<String> resend(@RequestBody OtpSendDto dto) {
        authService.resendOtp(dto);
        return ResponseEntity.ok("OTP göndərildi");
    }


    @PostMapping("/verify-otpp")
    public ResponseEntity<String> verifyOtpp(@RequestBody OtpVerifyRequestDto dto){
        authService.verifyOtp(dto);
        return ResponseEntity.ok("Account activated");
    }



    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody UserLoginRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<TokenResponseDto> refresh(@RequestBody RequestRefreshToken dto) {
        return ResponseEntity.ok(authService.refreshToken(dto.getRefreshToken()));
    }



//    @PostMapping("/resend")
//    public void resend(@RequestBody OtpSendDto dto){
//
//    }


}
