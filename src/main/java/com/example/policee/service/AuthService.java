package com.example.policee.service;


import com.example.policee.dao.entity.UserEntity;
import com.example.policee.dao.repository.UserRepository;
import com.example.policee.dto.request.OtpSendDto;
import com.example.policee.dto.request.OtpVerifyRequestDto;
import com.example.policee.dto.request.UserLoginRequestDto;
import com.example.policee.dto.request.UserRegisterRequestDto;
import com.example.policee.dto.response.TokenResponseDto;
import com.example.policee.mapper.UserMapper;
import com.example.policee.util.config.config.JwtService;
import com.example.policee.util.config.enums.Status;
import com.example.policee.util.config.exception.UserNotFoundException;
import com.example.policee.util.config.exception.userAlreadyExist;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final OtpSendService otpSeervice;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;




    public void saveUser(UserRegisterRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new userAlreadyExist("Bu user artıq mövcuddur");
        }

        UserEntity user = userMapper.dtoToEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(Status.PENDING);
        user.setVerifyCount(0);

        userRepository.save(user);

        otpSeervice.sendRegisterOtp(dto.getEmail());
    }




    public void verifyOtp(OtpVerifyRequestDto dto) {
        otpSeervice.verifyOtp(dto);
    }



    public void resendOtpp(OtpSendDto dto) {
        otpSeervice.resendOtp(dto);
    }

    public void resendOtp(OtpSendDto dto) {
        otpSeervice.resendOtp(dto); // OtpSendService daxilindəki yeni metodu çağırır
    }









    public TokenResponseDto login(UserLoginRequestDto dto) {
        UserEntity user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email və ya şifrə yanlışdır"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email və ya şifrə yanlışdır");
        }


        if (user.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("Zəhmət olmasa əvvəlcə hesabınızı email vasitəsilə təsdiqləyin");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponseDto(accessToken, refreshToken);
    }



    public TokenResponseDto refreshToken(String oldRefreshToken) {
        if (!jwtService.isTokenValid(oldRefreshToken)) {
            throw new RuntimeException("Refresh tokenin vaxtı bitib, yenidən login olun.");
        }

        String email = jwtService.extractEmail(oldRefreshToken);

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new RuntimeException("Hesab aktiv deyil.");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }






}
