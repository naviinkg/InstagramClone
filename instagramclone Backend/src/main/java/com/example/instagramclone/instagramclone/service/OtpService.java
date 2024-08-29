package com.example.instagramclone.instagramclone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Random;

@Service
public class OtpService {

    // Temporary storage for OTPs
    private ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();

    @Autowired
    private EmailService emailService;

    public String generateOtp() {
        // Generate a random 6-digit OTP
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtp(String email, String otp) throws Exception {

        try {
            // Send OTP to email
            emailService.sendOtp(email, otp);
            // Store OTP temporarily
            otpStore.put(email, otp);
        } catch (Exception e) {
            System.out.println("before");
            throw e;
        }

    }

    public boolean verifyOtp(String email, String otp) {
        // Retrieve stored OTP
        String storedOtp = otpStore.get(email);

        // Check if provided OTP matches the stored OTP
        if (storedOtp != null && storedOtp.equals(otp)) {
            // OTP is valid
            otpStore.remove(email); // Remove OTP after successful verification
            return true;
        } else {
            // OTP is invalid
            return false;
        }
    }
}
