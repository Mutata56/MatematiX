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
 * Класс, описывающий провайдера ауентификации на сайте. Настраивается в основной конфигурации.
 * @see mutata.com.github.MatematixProject.configuration.SecurityConfiguration
 */

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    /**
     * Объект для работы с БД Wrapper класса пользователя
     */

    private final MyUserDetailsService service;

    /**
     * Кодировщик пароля
     */
    private final PasswordEncoder encoder;
    @Autowired
    public AuthenticationProviderImpl(MyUserDetailsService myUserDetailsService, PasswordEncoder encoder) {
        this.service = myUserDetailsService;
        this.encoder = encoder;

    }

    /**
     * Метод ауентификации пользователя на сайт
     * @param authentication - данные, которые ввёл пользователь
     * @throws BadCredentialsException - ошибка, возникающая при вводе неправильных данных: логин/пароль
     * @return Ауентификацию по паролю, которая присваивается пользователю
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        UserDetails details; // Wrapper класс
        if(!username.contains("@"))
              details = service.loadUserByUsername(username); // Если не почта, ищем по юзернейму
        else
             details = service.loadUserByEmail(username); // Ищем по почте
        if(!encoder.matches(authentication.getCredentials().toString(),details.getPassword())) // Если закодированный пароль не совпадает с закодированным введённым паролем
            throw new BadCredentialsException("Неверный пароль!");
        return new UsernamePasswordAuthenticationToken(details,details.getPassword(), details.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
