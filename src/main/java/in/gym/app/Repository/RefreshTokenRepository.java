package in.gym.app.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import in.gym.app.Entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserEmail(String email);

    void deleteByUserEmail(String email);
}
