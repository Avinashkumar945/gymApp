package in.gym.app.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.gym.app.Dto.ApiResponse;
import in.gym.app.Dto.ForgotPasswordRequest;
import in.gym.app.Dto.LoginRequest;
import in.gym.app.Dto.OtpRequest;
import in.gym.app.Dto.ResendOtpRequest;
import in.gym.app.Dto.ResetPasswordRequest;
import in.gym.app.Dto.SignupRequest;
import in.gym.app.Entity.RefreshToken;
import in.gym.app.Entity.User;
import in.gym.app.Repository.IUserRepository;
import in.gym.app.Repository.RefreshTokenRepository;


@Service
@Transactional
public class UserService {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    // ====================================================
    // SIGNUP
    // ====================================================
    public ApiResponse register(SignupRequest request) {

        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser.isPresent()) {

            User user = existingUser.get();

            if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
                return new ApiResponse(false,
                        "Email already registered",
                        409);
            }

            // Update unverified user
            user.setName(request.getName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setExperienceYears(request.getExperienceYears());
            user.setHeight(request.getHeight());
            user.setAge(request.getAge());
            user.setGymAvailable(request.getGymAvailable());
            user.setGoal(request.getGoal());
            user.setUserNameInApp(request.getUserNameInApp());

            String otp = generateOtp();

            user.setOtp(passwordEncoder.encode(otp));
            user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

            userRepository.save(user);
            emailService.sendOtp(user.getEmail(), otp);

            return new ApiResponse(true,
                    "OTP resent. Please verify your email.",
                    200);
        }

        // New user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setExperienceYears(request.getExperienceYears());
        user.setHeight(request.getHeight());
        user.setAge(request.getAge());
        user.setGymAvailable(request.getGymAvailable());
        user.setGoal(request.getGoal());
        user.setUserNameInApp(request.getUserNameInApp());
        user.setIsEmailVerified(false);

        String otp = generateOtp();

        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), otp);

        return new ApiResponse(true,
                "OTP sent to your email.",
                201);
    }

    private String generateOtp() {
        return String.valueOf((int) (Math.random() * 900000) + 100000);
    }

    // ====================================================
    // LOGIN
    // ====================================================
    public ApiResponse login(LoginRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return new ApiResponse(false, "Invalid credentials", 401);
        }

        User user = optionalUser.get();

        if (!Boolean.TRUE.equals(user.getIsEmailVerified())) {
            return new ApiResponse(false, "Please verify your email first", 403);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new ApiResponse(false, "Invalid credentials", 401);
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshTokenValue = jwtService.generateRefreshToken(user.getEmail());

        // 🔥 Delete old refresh token if exists
        refreshTokenRepository.deleteByUserEmail(user.getEmail());

        // 🔥 Create new RefreshToken entity
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(refreshToken);

        return new ApiResponse(true,
                "Login successful",
                200,
                accessToken,
                refreshTokenValue,
                1800);
    }


    // ====================================================
    // VERIFY OTP
    // ====================================================
    public ApiResponse verifyOtp(OtpRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return new ApiResponse(false, "Invalid request", 400);
        }

        User user = optionalUser.get();

        if (user.getOtpExpiry() == null ||
                user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            return new ApiResponse(false,
                    "OTP expired. Please resend OTP.",
                    400);
        }

        if (user.getOtp() == null ||
                !passwordEncoder.matches(request.getOtp(), user.getOtp())) {
            return new ApiResponse(false, "Invalid OTP", 400);
        }

        user.setIsEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return new ApiResponse(true,
                "Email verified successfully",
                200);
    }

    // ====================================================
    // RESEND OTP
    // ====================================================
    public ApiResponse resendOtp(ResendOtpRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return new ApiResponse(false, "Invalid request", 400);
        }

        User user = optionalUser.get();

        if (Boolean.TRUE.equals(user.getIsEmailVerified())) {
            return new ApiResponse(false,
                    "Email already verified",
                    409);
        }

        String newOtp = generateOtp();

        user.setOtp(passwordEncoder.encode(newOtp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);
        emailService.sendOtp(user.getEmail(), newOtp);

        return new ApiResponse(true,
                "New OTP sent successfully",
                200);
    }

    // ====================================================
    // REFRESH TOKEN
    // ====================================================
    public ApiResponse refreshToken(String refreshToken) {

        try {

            String email = jwtService.extractEmail(refreshToken);

            Optional<RefreshToken> storedToken =
                    refreshTokenRepository.findByUserEmail(email);

            if (storedToken.isEmpty() ||
                !passwordEncoder.matches(refreshToken,
                        storedToken.get().getToken()) ||
                storedToken.get().getExpiryDate().isBefore(LocalDateTime.now())) {

                return new ApiResponse(false,
                        "Invalid refresh token",
                        401);
            }

            String newAccessToken = jwtService.generateAccessToken(email);

            return new ApiResponse(true,
                    "Token refreshed",
                    200,
                    newAccessToken,
                    refreshToken,
                    1800);

        } catch (Exception e) {

            return new ApiResponse(false,
                    "Invalid or expired refresh token",
                    401);
        }
    }


    // ====================================================
    // LOGOUT
    // ====================================================
    public ApiResponse logout(String refreshToken) {

        try {

            String email = jwtService.extractEmail(refreshToken);

            refreshTokenRepository.deleteByUserEmail(email);

            return new ApiResponse(true,
                    "Logged out successfully",
                    200);

        } catch (Exception e) {

            return new ApiResponse(false,
                    "Invalid request",
                    400);
        }
    }


    // ====================================================
    // CLEANUP UNVERIFIED USERS
    // ====================================================
    @Scheduled(cron = "0 0 * * * *")
    public void deleteUnverifiedUsers() {

        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);

        userRepository.deleteByIsEmailVerifiedFalseAndOtpExpiryBefore(cutoffTime);
    }
    
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return new ApiResponse(false,
                    "If email exists, OTP has been sent",
                    200); 
            // Don't reveal whether email exists (security)
        }

        User user = optionalUser.get();

        String otp = generateOtp();

        user.setOtp(passwordEncoder.encode(otp));
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        emailService.sendOtp(user.getEmail(), otp);

        return new ApiResponse(true,
                "OTP sent to your email",
                200);
    }
    
    public ApiResponse resetPassword(ResetPasswordRequest request) {

        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());

        if (optionalUser.isEmpty()) {
            return new ApiResponse(false,
                    "Invalid request",
                    400);
        }

        User user = optionalUser.get();

        if (user.getOtp() == null ||
            user.getOtpExpiry() == null ||
            user.getOtpExpiry().isBefore(LocalDateTime.now())) {

            return new ApiResponse(false,
                    "OTP expired. Please request again.",
                    400);
        }

        if (!passwordEncoder.matches(request.getOtp(), user.getOtp())) {
            return new ApiResponse(false,
                    "Invalid OTP",
                    400);
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // Clear OTP
        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        return new ApiResponse(true,
                "Password reset successful",
                200);
    }


}
