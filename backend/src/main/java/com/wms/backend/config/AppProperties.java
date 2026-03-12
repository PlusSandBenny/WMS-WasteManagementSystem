package com.wms.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final Otp otp = new Otp();
    private final Billing billing = new Billing();
    private final Uploads uploads = new Uploads();

    public Jwt getJwt() {
        return jwt;
    }

    public Otp getOtp() {
        return otp;
    }

    public Billing getBilling() {
        return billing;
    }

    public Uploads getUploads() {
        return uploads;
    }

    public static class Jwt {
        private long accessTtlSeconds = 900;
        private long refreshTtlSeconds = 2592000;

        public long getAccessTtlSeconds() {
            return accessTtlSeconds;
        }

        public void setAccessTtlSeconds(long accessTtlSeconds) {
            this.accessTtlSeconds = accessTtlSeconds;
        }

        public long getRefreshTtlSeconds() {
            return refreshTtlSeconds;
        }

        public void setRefreshTtlSeconds(long refreshTtlSeconds) {
            this.refreshTtlSeconds = refreshTtlSeconds;
        }
    }

    public static class Otp {
        private boolean returnCode = true;

        public boolean isReturnCode() {
            return returnCode;
        }

        public void setReturnCode(boolean returnCode) {
            this.returnCode = returnCode;
        }
    }

    public static class Billing {
        private BigDecimal defaultMonthlyFeeNgn = new BigDecimal("2000");

        public BigDecimal getDefaultMonthlyFeeNgn() {
            return defaultMonthlyFeeNgn;
        }

        public void setDefaultMonthlyFeeNgn(BigDecimal defaultMonthlyFeeNgn) {
            this.defaultMonthlyFeeNgn = defaultMonthlyFeeNgn;
        }
    }

    public static class Uploads {
        private String dir = "uploads";

        public String getDir() {
            return dir;
        }

        public void setDir(String dir) {
            this.dir = dir;
        }
    }
}

