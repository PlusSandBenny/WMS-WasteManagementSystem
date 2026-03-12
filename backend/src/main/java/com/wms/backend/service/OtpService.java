package com.wms.backend.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final Duration OTP_TTL = Duration.ofMinutes(10);
    private static final Duration MIN_INTERVAL = Duration.ofSeconds(30);

    private final SecureRandom random = new SecureRandom();
    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();

    public String issueOtp(String destination) {
        Instant now = Instant.now();
        OtpEntry existing = store.get(destination);
        if (existing != null && existing.issuedAt.plus(MIN_INTERVAL).isAfter(now)) {
            // Re-issue same OTP within throttle window to avoid spamming.
            return existing.code;
        }
        String code = String.format("%06d", random.nextInt(1_000_000));
        store.put(destination, new OtpEntry(code, now, now.plus(OTP_TTL)));
        return code;
    }

    public boolean verifyOtp(String destination, String otp) {
        OtpEntry entry = store.get(destination);
        if (entry == null) {
            return false;
        }
        Instant now = Instant.now();
        if (entry.expiresAt.isBefore(now)) {
            store.remove(destination);
            return false;
        }
        boolean ok = entry.code.equals(otp);
        if (ok) {
            store.remove(destination);
        }
        return ok;
    }

    private record OtpEntry(String code, Instant issuedAt, Instant expiresAt) {
    }
}

