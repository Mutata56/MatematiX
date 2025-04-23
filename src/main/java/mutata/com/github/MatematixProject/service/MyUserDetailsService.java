package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.repository.UserRepository;
import mutata.com.github.MatematixProject.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * Сервис, связанный с Wrapper классом User
 * @see MyUserDetails
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */


@Service
@Transactional(readOnly = true)
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Загрузка юзера по юзернейму
     * @throws UsernameNotFoundException - ошибка "Юзер не найден"
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = repository.findUserByNameIgnoreCase(username);
        if(optional.isEmpty())
            throw new UsernameNotFoundException("Пользователь не найден!");
        else
            return new MyUserDetails(optional.get());

    }

    /**
     * Загрузка юзера по почте
     * @throws UsernameNotFoundException - ошибка "Юзер не найден"
     */

    public UserDetails loadUserByEmail(String email) {
        Optional<User> optional = repository.findUserByEmailIgnoreCase(email);
        if(optional.isEmpty())
            throw new UsernameNotFoundException("Пользователь не найден!");
        else
            return new MyUserDetails(optional.get());
    }
}
