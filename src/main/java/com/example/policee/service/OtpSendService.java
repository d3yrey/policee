package com.example.policee.service;


import com.example.policee.dao.entity.UserEntity;
import com.example.policee.dao.repository.UserRepository;
import com.example.policee.dto.request.OtpSendDto;
import com.example.policee.dto.request.OtpVerifyRequestDto;
import com.example.policee.dto.response.OtpResDto;
import com.example.policee.util.config.enums.Status;
import com.example.policee.util.config.exception.OtpCodeIsNotCorrectException;
import com.example.policee.util.config.exception.UserBlockaException;
import com.example.policee.util.config.exception.UserNotFoundException;
import com.example.policee.util.config.helper.EmailCodeCreater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor

public class OtpSendService {

    private final EmailSender emailSender;
    private final UserRepository repo;





    /**
     * İlk qeydiyyat zamanı OTP göndərmək üçün.
     */
    public OtpResDto sendRegisterOtp(String email) {
        UserEntity user = repo.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User tapılmadı"));

        return processOtpRequest(user);
    }

    /**
     * Resend düyməsi sıxıldıqda OTP göndərmək üçün.
     */
    public void resendOtp(OtpSendDto dto) {
        UserEntity user = repo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User tapılmadı"));

        if (user.getStatus() == Status.ACTIVE) {
            throw new RuntimeException("User artıq aktivdir");
        }

        processOtpRequest(user);
    }

    /**
     * OTP-ni yoxlayan əsas metod.
     */
    public void verifyOtp(OtpVerifyRequestDto dto) {
        UserEntity user = repo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User tapılmadı"));

        // 1. Blok yoxlanışı
        if (user.getStatus() == Status.BLOCK &&
                user.getBlocktime() != null &&
                user.getBlocktime().isAfter(LocalDateTime.now())) {
            throw new UserBlockaException("Hesab blokdadır. Blokun bitmə vaxtı: " + user.getBlocktime());
        }

        // 2. Artıq aktivdirsə
        if (user.getStatus() == Status.ACTIVE) {
            throw new RuntimeException("Bu hesab artıq təsdiqlənib");
        }

        // 3. Vaxt yoxlanışı
        if (user.getOtpExpireTime() == null || user.getOtpExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP vaxtı bitib, yenidən kod göndərin");
        }

        // 4. Kodun doğruluğu
        if (!dto.getOtp().equals(user.getOtpCode())) {
            handleWrongOtp(user);
            throw new OtpCodeIsNotCorrectException("OTP yanlışdır!");
        }

        // 5. UĞURLU HAL (Success)
        completeVerification(user);
    }

    // --- KÖMƏKÇİ (PRIVATE) METODLAR ---

    private OtpResDto processOtpRequest(UserEntity user) {
        // Blok yoxlanışı və avtomatik təmizləmə
        if (user.getStatus() == Status.BLOCK) {
            if (user.getBlocktime() != null && user.getBlocktime().isAfter(LocalDateTime.now())) {
                throw new UserBlockaException("Hələ blokdasınız. Gözləyin: " + user.getBlocktime());
            }
            resetUserOtpStatus(user);
        }

        // Əgər istifadəçi çox cəhd edibsə (verifyCount >= 5)
        if (user.getVerifyCount() != null && user.getVerifyCount() >= 5) {
            applyBlock(user);
            return responseDto(user);
        }

        // Normal OTP hazırlığı
        String otp = EmailCodeCreater.createCode();
        user.setOtpCode(otp);
        user.setOtpExpireTime(LocalDateTime.now().plusMinutes(5));
        user.setLogintime(LocalDateTime.now());

        // Hər kod istəyində sayğacı artırırıq
        user.setVerifyCount(user.getVerifyCount() == null ? 1 : user.getVerifyCount() + 1);

        repo.save(user);
        emailSender.sendOtpEmail(user.getEmail(), otp);

        return responseDto(user);
    }

    private void handleWrongOtp(UserEntity user) {
        user.setVerifyCount(user.getVerifyCount() == null ? 1 : user.getVerifyCount() + 1);

        if (user.getVerifyCount() >= 5) {
            applyBlock(user);
        } else {
            repo.save(user);
        }
    }

    private void applyBlock(UserEntity user) {
        user.setStatus(Status.BLOCK);
        user.setBlocktime(LocalDateTime.now().plusMinutes(5));
        user.setOtpCode(null); // Bloklananda köhnə kodu silirik
        repo.save(user);
    }

    private void resetUserOtpStatus(UserEntity user) {
        user.setStatus(Status.PENDING);
        user.setVerifyCount(0); // Blok bitibsə, limit sıfırlanır
        user.setBlocktime(null);
        // repo.save(user); // Sonda ana metod tərəfindən save olunur
    }

    private void completeVerification(UserEntity user) {
        user.setStatus(Status.ACTIVE);
        user.setOtpCode(null);
        user.setOtpExpireTime(null);
        user.setVerifyCount(0);
        user.setBlocktime(null);
        repo.save(user);
    }

    private OtpResDto responseDto(UserEntity user) {
        return new OtpResDto(
                user.getStatus(),
                user.getBlocktime(),
                user.getLogintime()
        );
    }
}