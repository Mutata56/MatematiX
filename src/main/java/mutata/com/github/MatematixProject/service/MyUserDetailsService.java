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
 * Сервис для загрузки деталей пользователя,
 * необходимый для Spring Security.
 * <p>Реализует {@link UserDetailsService} и предоставляет
 * методы поиска пользователя по имени или email
 * из {@link UserRepository} и обёртку в {@link MyUserDetails}.</p>
 *
 * <p>Аннотация {@link Service} регистрирует класс
 * как компонент Spring, а {@link Transactional}
 * с {@code readOnly = true} указывает на то,
 * что операции не изменяют состояние БД.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see MyUserDetails
 * @see UserDetailsService
 * @see UserRepository
 */
@Service
@Transactional(readOnly = true)
public class MyUserDetailsService implements UserDetailsService {

    /**
     * Репозиторий для доступа к сущности {@link User}.
     */
    private final UserRepository repository;

    /**
     * Конструктор для внедрения зависимости репозитория.
     *
     * @param repository репозиторий пользователей
     */
    @Autowired
    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Загружает пользователя по его логину (username).
     * <p>Используется Spring Security при входе.</p>
     *
     * @param username логин пользователя
     * @return объект {@link UserDetails} с данными для аутентификации
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = repository.findUserByNameIgnoreCase(username);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден!");
        }
        return new MyUserDetails(optional.get());
    }

    /**
     * Загружает пользователя по его электронной почте.
     * <p>Аналог метода {@link #loadUserByUsername(String)},
     * но принимает email вместо логина.</p>
     *
     * @param email email пользователя
     * @return объект {@link UserDetails} с данными для аутентификации
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public UserDetails loadUserByEmail(String email) {
        Optional<User> optional = repository.findUserByEmailIgnoreCase(email);
        if (optional.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден!");
        }
        return new MyUserDetails(optional.get());
    }
}