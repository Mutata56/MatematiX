package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;
/**
 * Репозиторий в контексте Spring, который автоматически создаёт методы для работы с соотв. БД (исходя из названия репозитория)
 */
@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,Long> {

    /**
     * Кастомный метод репозитория дла поиска токена по строковому литералу
     * @param token - строковой литерал, по которому нужно найти токен
     */

    Optional<ResetPasswordToken> findByToken(String token);

    /**
     * Кастомный метод репозитория дла удаления токена по его строковому литералу
     * @param token - строковой литерал, по которому нужно удалить токен
     */

    void deleteByToken(String token);

    /**
     * Кастомный метод репозитория дла удаления токена по его строковому литералу
     * Modifying - позволяет модифицировать запросы: обновлять, удалять данные и расширять возможности аннотации @Query в JpaRepository
     * Query - MySQL запрос
     * @param date - Токены, чья expiration дата будет меньше, чем данный параметр будут удалены из БД
     *
     */

    @Modifying
    @Query("DELETE FROM ResetPasswordToken t WHERE t.expirationDate <= ?1")
    void deleteByExpirationDateLessThan(LocalDateTime date);
}
