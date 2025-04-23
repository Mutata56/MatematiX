package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Репозиторий в контексте Spring, который автоматически создаёт методы для работы с соотв. БД (исходя из названия репозитория)
 */
@Repository
public interface AvatarInfoRepository extends JpaRepository<AvatarInfo,String> {
    /**
     * Кастомный метод репозитория дла поиска информации аватарки по юзернейму пользователя
     * @param username - юзернейм, чью информацию нужно найти
     */
    Optional<AvatarInfo> findByUsername(String username);
}
