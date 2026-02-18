package in.gym.app.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.gym.app.Dto.ApiResponse;
import in.gym.app.Dto.ForgotPasswordRequest;
import in.gym.app.Dto.LoginRequest;
import in.gym.app.Dto.OtpRequest;
import in.gym.app.Dto.ResendOtpRequest;
import in.gym.app.Dto.ResetPasswordRequest;
import in.gym.app.Dto.SignupRequest;
import in.gym.app.Service.EmailService;
import in.gym.app.Service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")   // Allow frontend to connect
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // =========================
    // SIGNUP API
    // =========================
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> register(@RequestBody SignupRequest request) {

        ApiResponse response = userService.register(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // =========================
    // LOGIN API
    // =========================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {

        ApiResponse response = userService.login(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // =========================
    // VERIFY OTP API
    // =========================
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody OtpRequest request) {

        ApiResponse response = userService.verifyOtp(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // =========================
    // RESEND OTP API
    // =========================
    @PostMapping("/resend-otp")
    public ResponseEntity<ApiResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {

        ApiResponse response = userService.resendOtp(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    // =========================
    // TEST EMAIL (Optional - for testing only)
    // =========================
    @GetMapping("/test-email")
    public String testEmail() {

        emailService.sendOtp("your_real_email@gmail.com", "123456");

        return "Test email sent!";
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@RequestBody String refreshToken) {

        ApiResponse response = userService.refreshToken(refreshToken);

        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        ApiResponse response = userService.forgotPassword(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        ApiResponse response = userService.resetPassword(request);

        return ResponseEntity.status(response.getStatus()).body(response);
    }


}
