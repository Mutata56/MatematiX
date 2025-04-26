package mutata.com.github.MatematixProject.security;

import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Перехватчик ошибок доступа, вызываемый при попытке
 * выполнить запрещённую операцию пользователем.
 * <p>Реализует интерфейс {@link org.springframework.security.web.access.AccessDeniedHandler}
 * и перенаправляет пользователя на главную страницу
 * с параметром <code>nonValidUser=true</code>.</p>
 *
 * Настраивается в {@link mutata.com.github.MatematixProject.configuration.SecurityConfiguration}:
 * <pre>
 *   .exceptionHandling().accessDeniedHandler(new AccessDeniedHandler())
 * </pre>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see org.springframework.security.web.access.AccessDeniedHandler
 * @see mutata.com.github.MatematixProject.configuration.SecurityConfiguration
 */
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    /**
     * Обрабатывает исключение AccessDeniedException.
     * <p>Перенаправляет пользователя на URL:
     * <code>{contextPath}/index?nonValidUser=true</code>.</p>
     *
     * @param request  текущий HTTP-запрос
     * @param response текущий HTTP-ответ
     * @param accessDeniedException причина отказа в доступе
     * @throws IOException      при ошибках ввода-вывода при редиректе
     * @throws ServletException при ошибках сервлета
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        response.sendRedirect(
                request.getContextPath() + "/index?nonValidUser=true"
        );
    }
}