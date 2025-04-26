package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.token.Token;

/**
 * Базовый интерфейс для сервисов работы с токенами.
 * <p>Объявляет общие операции поиска, удаления и сохранения
 * над любым типом токена, реализующим {@link Token}.</p>
 *
 * @param <T> тип токена, расширяющий {@link Token}
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see Token
 */
public interface TokenService<T extends Token> extends MyService<T> {

    /**
     * Ищет токен по его строковому представлению.
     *
     * @param token строковое значение токена
     * @return объект токена или {@code null}, если не найден
     */
    Token findByToken(String token);

    /**
     * Удаляет токен из хранилища по его строковому представлению.
     *
     * @param token строковое значение токена для удаления
     */
    void delete(String token);

    /**
     * Сохраняет или обновляет объект токена в хранилище.
     *
     * @param token объект токена для сохранения
     */
    void save(Token token);
}