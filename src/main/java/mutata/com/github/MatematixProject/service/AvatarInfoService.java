
package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.repository.AvatarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Сервис для работы с данными аватарок пользователей.
 * <p>Обеспечивает поиск, сохранение и предоставление
 * информации об аватарках. Использует {@link AvatarInfoRepository}
 * для доступа к данным в БД.</p>
 *
 * <p>Аннотация {@link Transactional} с параметром<br>
 * {@code readOnly = true} указывает, что методы класса
 * по умолчанию выполняются в режиме только чтения.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see AvatarInfo
 * @see AvatarInfoRepository
 */
@Service
@Transactional(readOnly = true)
public class AvatarInfoService {

    /**
     * Репозиторий для доступа к сущностям {@link AvatarInfo}.
     */
    private final AvatarInfoRepository repository;

    /**
     * Конструктор для внедрения зависимости репозитория.
     *
     * @param repository репозиторий аватарок
     */
    @Autowired
    public AvatarInfoService(AvatarInfoRepository repository) {
        this.repository = repository;
    }

    /**
     * Ищет информацию об аватарке пользователя по имени.
     *
     * @param username логин пользователя
     * @return {@link Optional} с сущностью {@link AvatarInfo},
     *         если запись найдена, иначе пустой Optional
     */
    public Optional<AvatarInfo> findByName(String username) {
        return repository.findByUsername(username);
    }

    /**
     * Сохраняет или обновляет информацию об аватарке пользователя.
     * <p>Аннотирован методом {@link Transactional}
     * с {@code readOnly = false}, так как изменяет данные в БД.</p>
     *
     * @param theInfo объект {@link AvatarInfo} для сохранения
     */
    @Transactional(readOnly = false)
    public void save(AvatarInfo theInfo) {
        repository.save(theInfo);
    }

    /**
     * Возвращает информацию об аватарке по логину,
     * либо создаёт и возвращает пустой объект,
     * если запись отсутствует в БД.
     *
     * @param name логин пользователя
     * @return существующий {@link AvatarInfo} или новый с заданным именем
     */
    public AvatarInfo findByNameOrReturnEmpty(String name) {
        var result = findByName(name).orElse(null);
        if (result == null) {
            result = new AvatarInfo(name);
        }
        return result;
    }
}