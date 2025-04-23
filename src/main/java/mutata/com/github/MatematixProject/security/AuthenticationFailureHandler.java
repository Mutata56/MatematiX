package mutata.com.github.MatematixProject.security;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Класс, описывающий перехватчик ошибок типа "Ошибка во время ауентификации". Настраивается в основной конфигурации
 * @see mutata.com.github.MatematixProject.configuration.SecurityConfiguration
 */

public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {

    /**
     * Если произошла ошибка ауетнификации, отправляем пользователя на одну из соотв. страниц.
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception.getMessage().equals("Пользователь не найден!"))
            response.sendRedirect(request.getContextPath() + "/auth/login?userNotFound");
        else if(exception.getMessage().equals("Неверный пароль!"))
            response.sendRedirect(request.getContextPath() + "/auth/login?incorrectPassword");
        else response.sendRedirect(request.getContextPath() + "/");  // auth/login?error ?
    }
}
