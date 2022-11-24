package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    Optional<VerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.token = ?1")
    void deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expirationDate <= ?1")
    void deleteByExpirationDateLessThan(LocalDateTime date);
}
