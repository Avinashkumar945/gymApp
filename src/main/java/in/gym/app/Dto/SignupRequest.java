package in.gym.app.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class SignupRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Optional fields
    private Integer experienceYears;

    private Float height;

    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 100, message = "Age must be less than 100")
    private Integer age;

    private Boolean gymAvailable;

    private String goal;

    private String userNameInApp;

    public SignupRequest() {
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getGymAvailable() {
        return gymAvailable;
    }

    public void setGymAvailable(Boolean gymAvailable) {
        this.gymAvailable = gymAvailable;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getUserNameInApp() {
        return userNameInApp;
    }

    public void setUserNameInApp(String userNameInApp) {
        this.userNameInApp = userNameInApp;
    }
}
