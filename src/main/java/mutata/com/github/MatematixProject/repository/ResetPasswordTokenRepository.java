package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий для управления сущностями {@link ResetPasswordToken}.
 * <p>Наследует {@link JpaRepository}, предоставляя стандартные
 * CRUD-операции, а также дополнительные методы для работы с
 * токенами сброса пароля.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see ResetPasswordToken
 * @see JpaRepository
 */
@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {

    /**
     * Ищет {@link ResetPasswordToken} по строковому значению токена.
     *
     * @param token строковый литерал токена
     * @return {@link Optional} с найденным токеном или пустой, если не найден
     */
    Optional<ResetPasswordToken> findByToken(String token);

    /**
     * Удаляет запись {@link ResetPasswordToken} по строковому значению токена.
     *
     * @param token строковый литерал токена для удаления
     */
    void deleteByToken(String token);

    /**
     * Удаляет все токены, у которых дата истечения меньше или равна указанной.
     * <p>Использует аннотацию {@link Modifying} для модификации данных
     * и {@link Query} для выполнения кастомного JPQL-запроса.</p>
     *
     * @param date пороговая дата истечения токенов
     */
    @Modifying
    @Query("DELETE FROM ResetPasswordToken t WHERE t.expirationDate <= ?1")
    void deleteByExpirationDateLessThan(LocalDateTime date);
}