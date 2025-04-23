package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.repository.AvatarInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Сервис информации об аватарках юзеров.
 * @see AvatarInfo
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */
@Service
@Transactional(readOnly = true)
public class AvatarInfoService {

    private final AvatarInfoRepository repository;

    @Autowired
    public AvatarInfoService(AvatarInfoRepository repository) {
        this.repository = repository;
    }

    /**
     * Поиск информации об аватарке юзера по его никнейму
     * @param username - никнейм, по которому необходимо найти информацию.
     */
    public Optional<AvatarInfo> findByName(String username) {
        return repository.findByUsername(username);
    }

    /**
     * Сохранение информации об аватарке юзера.
     * Transactional(readOnly = false) - данный метод изменяет БД.
     * @param theInfo - информацию, которую необходимо сохранить в БД
     */

    @Transactional(readOnly = false)
    public void save(AvatarInfo theInfo) {
        repository.save(theInfo);
    }

    /**
     * Найти информацию по имени или вернуть пустой (стандартный) аватар
     * @param name - имя, по которому будет идти поиск
     */

    public AvatarInfo findByNameOrReturnEmpty(String name) {
        var result = findByName(name).orElse(null);
        if(result == null)
            result = new AvatarInfo(name);
        return result;
    }
}
