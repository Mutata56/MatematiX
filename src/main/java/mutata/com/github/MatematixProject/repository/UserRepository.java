package mutata.com.github.MatematixProject.repository;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
/**
 * Репозиторий в контексте Spring, который автоматически создаёт методы для работы с соотв. БД (исходя из названия репозитория)
 */
@Repository
public interface UserRepository extends JpaRepository<User,String> {

     /**
      * Кастомный метод репозитория дла поиска юзера по логину
      */

     Optional<User> findUserByNameIgnoreCase(String name);

     /**
      * Кастомный метод репозитория дла поиска юзера по почте
      */

     Optional<User> findUserByEmailIgnoreCase(String email);
}
