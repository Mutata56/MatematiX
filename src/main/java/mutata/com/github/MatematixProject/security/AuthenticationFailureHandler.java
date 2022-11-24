package mutata.com.github.MatematixProject.security;

import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        System.out.println(exception);
        if(exception.getMessage().equals("Пользователь не найден!"))
            response.sendRedirect(request.getContextPath() + "/auth/login?userNotFound");
        else if(exception.getMessage().equals("Неверный пароль!"))
            response.sendRedirect(request.getContextPath() + "/auth/login?incorrectPassword");
        else response.sendRedirect(request.getContextPath() + "/");  // auth/login?error ?
    }
}
