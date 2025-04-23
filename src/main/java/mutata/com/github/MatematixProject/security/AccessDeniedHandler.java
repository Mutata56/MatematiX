package mutata.com.github.MatematixProject.security;

import org.springframework.security.access.AccessDeniedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Класс, описывающий перехватчик ошибок типа "Доступ запрещён". Настраивается в основной конфигурации
 * @see mutata.com.github.MatematixProject.configuration.SecurityConfiguration
 */
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    /**
     * Если пользователю запрещён доступ - отправляем его на начальную страницу с соотв. параметром
     */

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
         response.sendRedirect(request.getContextPath() + "/index?nonValidUser=true");
    }
}
