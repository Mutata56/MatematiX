
package mutata.com.github.MatematixProject.entity.token;

import mutata.com.github.MatematixProject.entity.User;

import java.time.LocalDateTime;

/**
 * Общий интерфейс для токенов системы аутентификации и верификации.
 * <p>Любая реализация {@code Token} должна предоставлять информацию о
 * пользователе, для которого создан токен, строковое представление токена
 * и дату его истечения.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
public interface Token {

    /**
     * Возвращает связанного пользователя, которому принадлежит токен.
     *
     * @return сущность {@link User}, для которой создан этот токен
     */
    User getUser();

    /**
     * Возвращает имя пользователя (логин) связанного пользователя.
     * <p>Должно быть эквивалентно {@code getUser().getName()}.</p>
     *
     * @return логин пользователя, которому принадлежит токен
     */
    String getUserName();

    /**
     * Устанавливает пользователя, который будет владельцем токена.
     *
     * @param user сущность {@link User}, для которой создаётся токен
     */
    void setUser(User user);

    /**
     * Возвращает дату и время, когда токен перестаёт быть действительным.
     *
     * @return {@link LocalDateTime} момента истечения токена
     */
    LocalDateTime getExpirationDate();

    /**
     * Устанавливает строковое представление самого токена.
     * <p>Используется для генерации и проверки токена при запросах.</p>
     *
     * @param token строка, однозначно идентифицирующая токен
     */
    void setToken(String token);

    /**
     * Устанавливает дату и время истечения действия токена.
     *
     * @param time {@link LocalDateTime} момента, после которого токен недействителен
     */
    void setExpirationDate(LocalDateTime time);

    /**
     * Возвращает строку токена, используемую в ссылках и запросах.
     *
     * @return строковое значение токена
     */
    String getToken();
}