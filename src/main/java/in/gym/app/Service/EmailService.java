package in.gym.app.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    public void sendOtp(String toEmail, String otp) {

        try {
            System.out.println("Sending OTP to: " + toEmail);

            URL url = new URL("https://api.brevo.com/v3/smtp/email");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("api-key", apiKey);
            conn.setRequestProperty("content-type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = """
            {
              "sender": { "name": "Gym App", "email": "ourgymapp2026@gmail.com" },
              "to": [{ "email": "%s" }],
              "subject": "Gym App Email Verification",
              "htmlContent": "<h3>Your OTP is: %s</h3>"
            }
            """.formatted(toEmail, otp);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInputString.getBytes());
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Brevo API Response Code: " + responseCode);

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
