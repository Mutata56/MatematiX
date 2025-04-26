
package mutata.com.github.MatematixProject.security;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Перехватчик неудачных попыток аутентификации.
 * <p>Реализует интерфейс {@link org.springframework.security.web.authentication.AuthenticationFailureHandler}
 * и перенаправляет пользователя на соответствующие страницы
 * в зависимости от причины ошибки (пользователь не найден или неверный пароль).</p>
 *
 * Настраивается в {@link mutata.com.github.MatematixProject.configuration.SecurityConfiguration}:
 * <pre>
 *   .failureHandler(new AuthenticationFailureHandler())
 * </pre>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see org.springframework.security.web.authentication.AuthenticationFailureHandler
 * @see mutata.com.github.MatematixProject.configuration.SecurityConfiguration
 */
public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    /**
     * Обрабатывает исключение {@link AuthenticationException} при неудачной аутентификации.
     * <p>Перенаправляет пользователя в зависимости от текста сообщения исключения:</p>
     * <ul>
     *   <li>"Пользователь не найден!" → {@code /auth/login?userNotFound}</li>
     *   <li>"Неверный пароль!" → {@code /auth/login?incorrectPassword}</li>
     *   <li>Любая другая ошибка → {@code /}</li>
     * </ul>
     *
     * @param request   текущий HTTP-запрос
     * @param response  текущий HTTP-ответ
     * @param exception исключение, вызвавшее неудачную аутентификацию
     * @throws IOException      при ошибках ввода-вывода при редиректе
     * @throws ServletException при ошибках сервлета
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        if ("Пользователь не найден!".equals(exception.getMessage())) {
            response.sendRedirect(request.getContextPath() + "/auth/login?userNotFound");
        } else if ("Неверный пароль!".equals(exception.getMessage())) {
            response.sendRedirect(request.getContextPath() + "/auth/login?incorrectPassword");
        } else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }
}