package mutata.com.github.MatematixProject.entity.token;

import lombok.Getter;

/**
 * Перечисление типов токенов, используемых в системе аутентификации:
 * <ul>
 *   <li>{@link #RESET} — токен для сброса пароля;</li>
 *   <li>{@link #VERIFICATION} — токен для подтверждения email.</li>
 * </ul>
 * Каждый тип содержит строковое имя, соответствующее имени модели,
 * что позволяет использовать значение в URL или шаблонах.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
public enum TokenType {
    /**
     * Токен для сброса пароля пользователя.
     */
    RESET("resetPassword"),

    /**
     * Токен для подтверждения email пользователя.
     */
    VERIFICATION("verification");

    /**
     * Строковое представление типа токена, используемое в URL или шаблонах.
     */
    @Getter
    private final String modelName;

    /**
     * Конструктор для установки строкового имени типа.
     *
     * @param modelName строковое имя модели токена
     */
    TokenType(String modelName) {
        this.modelName = modelName;
    }
}