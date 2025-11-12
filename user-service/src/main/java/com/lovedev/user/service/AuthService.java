package com.lovedev.user.service;
import com.lovedev.user.model.dto.request.*;
import com.lovedev.user.model.dto.response.AuthResponse;
public interface AuthService {
    /**
     * Register new user
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Verify user email with token
     */
    void verifyEmail(String token);

    /**
     * Authenticate user and return tokens
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh access token using refresh token
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logout user and revoke refresh token
     */
    void logout(String refreshToken);

    /**
     * Send password reset email
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Reset password using token
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Resend email verification
     */
    void resendVerificationEmail(String email);
}
