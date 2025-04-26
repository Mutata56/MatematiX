package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий для управления сущностями {@link VerificationToken}.
 * <p>Наследует {@link JpaRepository}, предоставляя стандартные
 * CRUD-операции, а также дополнительные методы для поиска,
 * удаления по токену и очистки устаревших записей.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see VerificationToken
 * @see JpaRepository
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Ищет токен подтверждения пользователя по строковому значению.
     *
     * @param token строковый литерал токена
     * @return {@link Optional} с найденным {@link VerificationToken},
     *         или пустой {@code Optional}, если запись не найдена
     */
    Optional<VerificationToken> findByToken(String token);

    /**
     * Удаляет запись {@link VerificationToken} по строковому значению токена.
     * <p>Использует JPQL-запрос для удаления конкретной записи.</p>
     *
     * @param token строковый литерал токена для удаления
     */
    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.token = ?1")
    void deleteByToken(String token);

    /**
     * Удаляет все токены, у которых дата истечения меньше или равна указанной.
     * <p>Используется для очистки устаревших токенов.</p>
     *
     * @param date пороговая дата истечения токенов
     */
    @Modifying
    @Query("DELETE FROM VerificationToken t WHERE t.expirationDate <= ?1")
    void deleteByExpirationDateLessThan(LocalDateTime date);
}