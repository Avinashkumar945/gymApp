package in.gym.app.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gym.app.Entity.User;

public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    void deleteByIsEmailVerifiedFalseAndOtpExpiryBefore(LocalDateTime time);
}
