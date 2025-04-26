package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для управления сущностями {@link User}.
 * <p>Наследует {@link JpaRepository}, что предоставляет стандартные
 * CRUD-операции и возможность определения методов выборки по
 * имени метода.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see User
 * @see JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

     /**
      * Выполняет поиск пользователя по логину без учёта регистра символов.
      *
      * @param name логин пользователя (не зависит от регистра)
      * @return {@link Optional} с найденным {@link User},
      *         или пустой {@code Optional}, если пользователь не найден
      */
     Optional<User> findUserByNameIgnoreCase(String name);

     /**
      * Выполняет поиск пользователя по email без учёта регистра символов.
      *
      * @param email адрес электронной почты пользователя
      * @return {@link Optional} с найденным {@link User},
      *         или пустой {@code Optional}, если пользователь не найден
      */
     Optional<User> findUserByEmailIgnoreCase(String email);
}