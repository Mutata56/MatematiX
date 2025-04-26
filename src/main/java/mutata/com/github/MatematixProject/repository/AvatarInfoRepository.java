
package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для управления данными аватарок пользователей.
 * <p>Наследует {@link JpaRepository}, что позволяет выполнять
 * стандартные CRUD-операции, а также определять кастомные методы
 * выборки на основе имени метода.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see JpaRepository
 */
@Repository
public interface AvatarInfoRepository extends JpaRepository<AvatarInfo, String> {
    /**
     * Ищет информацию об аватарке по логину пользователя.
     *
     * @param username логин пользователя
     * @return {@link Optional} с сущностью {@link AvatarInfo},
     *         если запись найдена, или пустой {@code Optional} иначе
     */
    Optional<AvatarInfo> findByUsername(String username);
}