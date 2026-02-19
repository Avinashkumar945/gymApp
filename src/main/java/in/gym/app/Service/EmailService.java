package in.gym.app.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        try {
            System.out.println("Sending OTP to: " + toEmail);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ourgymapp2026@gmail.com"); // must be verified in Brevo
            message.setTo(toEmail);
            message.setSubject("Gym App Email Verification");
            message.setText("Your OTP is: " + otp);

            mailSender.send(message);

            System.out.println("OTP sent successfully via Brevo SMTP");

        } catch (Exception e) {
            System.out.println("Error sending email:");
            e.printStackTrace();
        }
    }
}
