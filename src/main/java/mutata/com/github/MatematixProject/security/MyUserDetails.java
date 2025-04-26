package mutata.com.github.MatematixProject.security;

import mutata.com.github.MatematixProject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Обёртка для сущности {@link User}, реализующая интерфейс {@link UserDetails}.
 * Предоставляет Spring Security информацию о пользователе:
 * его логин, пароль, права и статус аккаунта.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see User
 * @see UserDetails
 */
public class MyUserDetails implements UserDetails {

    /**
     * Внутренний объект пользователя, содержащий данные из БД.
     */
    private final User user;

    /**
     * Конструктор для обёртки пользователя.
     *
     * @param user сущность пользователя из БД
     */
    @Autowired
    public MyUserDetails(User user) {
        this.user = user;
    }

    /**
     * Возвращает список прав (ролей) пользователя.
     * <p>Извлекает роль из {@link User#getRole()} и
     * оборачивает её в {@link SimpleGrantedAuthority}.</p>
     *
     * @return коллекция прав пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority(user.getRole())
        );
    }

    /**
     * Возвращает зашифрованный пароль пользователя.
     *
     * @return строка с хешированным паролем
     */
    @Override
    public String getPassword() {
        return user.getEncryptedPassword();
    }

    /**
     * Возвращает логин (имя) пользователя.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return user.getName();
    }

    /**
     * Проверяет, не истёк ли аккаунт.
     * <p>Использует ту же логику, что и {@link #isAccountNonLocked()}.</p>
     *
     * @return {@code true}, если аккаунт действителен
     */
    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonLocked();
    }

    /**
     * Проверяет, не заблокирован ли аккаунт пользователя.
     *
     * @return {@code true}, если поле {@code blocked} в {@link User} равно 0
     */
    @Override
    public boolean isAccountNonLocked() {
        return user.getBlocked() == 0;
    }

    /**
     * Проверяет, не истёкли ли учётные данные пользователя.
     * <p>Использует ту же логику, что и {@link #isAccountNonLocked()}.</p>
     *
     * @return {@code true}, если учётные данные ещё действительны
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return isAccountNonLocked();
    }

    /**
     * Проверяет, активирован ли аккаунт пользователя (подтверждена ли почта).
     *
     * @return {@code true}, если поле {@code enabled} в {@link User} равно 1
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled() == 1;
    }

    /**
     * Возвращает строковое представление обёрнутого пользователя.
     *
     * @return строка с данными пользователя
     */
    @Override
    public String toString() {
        return "Details{" +
                "user=" + user +
                '}';
    }
}
