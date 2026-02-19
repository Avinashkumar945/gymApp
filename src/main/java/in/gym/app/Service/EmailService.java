package in.gym.app.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Transactional
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String apiKey;

    public void sendOtp(String toEmail, String otp) {

        try {
            URL url = new URL("https://api.resend.com/emails");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = String.format("""
                {
                  "from": "onboarding@resend.dev",
                  "to": "%s",
                  "subject": "Gym App Email Verification",
                  "html": "<strong>Your OTP is: %s</strong>"
                }
                """, toEmail, otp);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInputString.getBytes());
            }

            conn.getResponseCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
