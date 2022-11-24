package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,Long> {
    Optional<ResetPasswordToken> findByToken(String token);


    void deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM ResetPasswordToken t WHERE t.expirationDate <= ?1")
    void deleteByExpirationDateLessThan(LocalDateTime date);
}
