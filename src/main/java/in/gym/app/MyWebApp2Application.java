package in.gym.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class MyWebApp2Application {

	public static void main(String[] args) {
		SpringApplication.run(MyWebApp2Application.class, args);
	}

}
