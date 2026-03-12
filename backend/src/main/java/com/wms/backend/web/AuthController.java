package com.wms.backend.web;

import com.wms.backend.domain.enums.Role;
import com.wms.backend.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthService authService;
    private final boolean returnOtpInResponse;

    public AuthController(AuthService authService, @Value("${app.otp.return-code:true}") boolean returnOtpInResponse) {
        this.authService = authService;
        this.returnOtpInResponse = returnOtpInResponse;
    }

    public record RequestOtpRequest(@NotBlank String destination) {
    }

    public record RequestOtpResponse(String destination, String devOtp) {
    }

    @PostMapping("/request-otp")
    public ResponseEntity<RequestOtpResponse> requestOtp(@RequestBody RequestOtpRequest req) {
        String otp = authService.requestOtp(req.destination());
        String devOtp = returnOtpInResponse ? otp : null;
        return ResponseEntity.ok(new RequestOtpResponse(req.destination(), devOtp));
    }

    public record VerifyOtpRequest(@NotBlank String destination, @NotBlank String otp) {
    }

    public record VerifyOtpResponse(String accessToken, String refreshToken, boolean hasAddress, Role role) {
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest req) {
        AuthService.AuthTokens tokens = authService.verifyOtpAndLogin(req.destination(), req.otp());
        return ResponseEntity.ok(new VerifyOtpResponse(tokens.accessToken(), tokens.refreshToken(), tokens.hasAddress(), tokens.role()));
    }
}

