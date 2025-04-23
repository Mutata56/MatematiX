package mutata.com.github.MatematixProject.security;


import mutata.com.github.MatematixProject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

/**
 * Wrapper класс для класса User, предназначенный для ауентификации пользователя в системе.
 */

public class MyUserDetails implements UserDetails {

    private final User user;

    @Autowired
    public MyUserDetails(User user) {
        this.user = user;
    }

    /**
     * Получение роли юзера
     * @return роль юзера: ADMIN/USER
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonLocked();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getBlocked() == 0;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isAccountNonLocked();
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled() == 1;
    }

    @Override
    public String toString() {
        return "Details{" +
                "user=" + user +
                '}';
    }
}
