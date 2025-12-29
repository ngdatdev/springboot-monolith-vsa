package com.vsa.ecommerce.common.otp;

/**
 * Interface for OTP Service.
 */
public interface OtpService {

    /**
     * Generate OTP for a key (e.g., email).
     */
    String generateOtp(String key);

    /**
     * Validate OTP for a key.
     */
    boolean validateOtp(String key, String otp);

    /**
     * Invalidate (remove) OTP for a key.
     */
    void invalidateOtp(String key);
}
