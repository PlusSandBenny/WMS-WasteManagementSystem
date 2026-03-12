package com.wms.backend.service;

import com.wms.backend.config.AppProperties;
import com.wms.backend.domain.enums.Role;
import com.wms.backend.domain.model.User;
import com.wms.backend.repo.AddressRepository;
import com.wms.backend.repo.UserRepository;
import com.wms.backend.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final Duration accessTtl;
    private final Duration refreshTtl;

    public AuthService(
            UserRepository userRepository,
            AddressRepository addressRepository,
            OtpService otpService,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AppProperties appProperties
    ) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.accessTtl = Duration.ofSeconds(appProperties.getJwt().getAccessTtlSeconds());
        this.refreshTtl = Duration.ofSeconds(appProperties.getJwt().getRefreshTtlSeconds());
    }

    public String requestOtp(String destination) {
        // Delivery to SMS/Email provider happens outside Phase 1; this issues the OTP.
        return otpService.issueOtp(destination);
    }

    @Transactional
    public AuthTokens verifyOtpAndLogin(String destination, String otp) {
        boolean ok = otpService.verifyOtp(destination, otp);
        if (!ok) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        User user = findOrCreateUser(destination);
        boolean hasAddress = addressRepository.findByUserId(user.getId()).isPresent();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        String accessToken = jwtService.createToken(destination, claims, accessTtl);

        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("type", "refresh");
        refreshClaims.put("role", user.getRole().name());
        String refreshToken = jwtService.createToken(destination, refreshClaims, refreshTtl);

        return new AuthTokens(accessToken, refreshToken, hasAddress, user.getRole());
    }

    private User findOrCreateUser(String destination) {
        boolean isEmail = destination.contains("@");
        User user = isEmail
                ? userRepository.findByEmailIgnoreCase(destination).orElse(null)
                : userRepository.findByPhone(destination).orElse(null);

        if (user != null) {
            return user;
        }

        User created = new User();
        if (isEmail) {
            created.setEmail(destination);
        } else {
            created.setPhone(destination);
        }
        created.setRole(Role.RESIDENT);
        created.setPasswordHash(passwordEncoder.encode("OTP_LOGIN_ONLY"));
        return userRepository.save(created);
    }

    public record AuthTokens(String accessToken, String refreshToken, boolean hasAddress, Role role) {
    }
}
