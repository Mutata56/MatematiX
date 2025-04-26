
package mutata.com.github.MatematixProject.security;

import mutata.com.github.MatematixProject.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Провайдер аутентификации, реализующий логику проверки
 * учетных данных пользователя.
 * <p>Используется в конфигурации безопасности
 * {@link mutata.com.github.MatematixProject.configuration.SecurityConfiguration}.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see AuthenticationProvider
 * @see MyUserDetailsService
 */
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    /**
     * Сервис для загрузки деталей пользователя из БД
     * по логину или email.
     */
    private final MyUserDetailsService service;

    /**
     * Компонент для кодирования и проверки паролей.
     */
    private final PasswordEncoder encoder;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param myUserDetailsService сервис для загрузки пользователя
     * @param encoder               кодировщик пароля
     */
    @Autowired
    public AuthenticationProviderImpl(MyUserDetailsService myUserDetailsService,
                                      PasswordEncoder encoder) {
        this.service = myUserDetailsService;
        this.encoder = encoder;
    }

    /**
     * Основная логика аутентификации пользователя.
     * <p>Проверяет введённые учетные данные и при совпадении
     * возвращает объект {@link UsernamePasswordAuthenticationToken}.</p>
     *
     * @param authentication объект с введёнными именем и паролем
     * @return токен аутентификации с деталями пользователя и правами
     * @throws BadCredentialsException если пароль неверен
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        UserDetails details;
        // Определяем, искали ли пользователя по email или по логину
        if (!username.contains("@")) {
            details = service.loadUserByUsername(username);
        } else {
            details = service.loadUserByEmail(username);
        }
        // Проверка пароля: введённый vs. сохранённый (закодированный)
        if (!encoder.matches(authentication.getCredentials().toString(), details.getPassword())) {
            throw new BadCredentialsException("Неверный пароль!");
        }
        // Возвращаем токен с правами пользователя
        return new UsernamePasswordAuthenticationToken(
                details,
                details.getPassword(),
                details.getAuthorities()
        );
    }

    /**
     * Указывает, что данный провайдер поддерживает все типы аутентификации.
     *
     * @param authentication класс аутентификационных объектов
     * @return {@code true}, если поддерживается
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}