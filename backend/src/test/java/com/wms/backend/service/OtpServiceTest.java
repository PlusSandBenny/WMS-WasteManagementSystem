package com.wms.backend.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OtpServiceTest {

    @Test
    void issuesAndVerifiesOtp() {
        OtpService service = new OtpService();
        String dest = "user@example.com";

        String otp = service.issueOtp(dest);
        assertNotNull(otp);
        assertEquals(6, otp.length());

        assertFalse(service.verifyOtp(dest, "000000"));
        assertTrue(service.verifyOtp(dest, otp));
        assertFalse(service.verifyOtp(dest, otp), "OTP should be single-use");
    }
}

