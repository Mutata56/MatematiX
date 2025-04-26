package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.CommentAction;
import mutata.com.github.MatematixProject.entity.CommentActionId;
import mutata.com.github.MatematixProject.repository.CommentActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для управления действиями пользователей над комментариями.
 * <p>Предоставляет методы для поиска, сохранения и удаления
 * сущностей {@link CommentAction}. Использует
 * {@link CommentActionRepository} для взаимодействия с базой данных.</p>
 * <p>Аннотация {@link Transactional} с параметром
 * {@code readOnly = true} указывает, что методы класса
 * по умолчанию выполняются без изменения данных.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see CommentAction
 * @see CommentActionRepository
 */
@Service
@Transactional(readOnly = true)
public class CommentActionService {

    /**
     * Репозиторий для операций с {@link CommentAction}.
     */
    private final CommentActionRepository repo;

    /**
     * Конструктор для внедрения зависимости репозитория.
     *
     * @param repo репозиторий CommentAction
     */
    @Autowired
    public CommentActionService(CommentActionRepository repo) {
        this.repo = repo;
    }

    /**
     * Ищет действие пользователя над комментарием по составному идентификатору.
     *
     * @param id       идентификатор комментария
     * @param username логин пользователя, выполнившего действие
     * @return объект {@link CommentAction} или {@code null}, если запись не найдена
     */
    public CommentAction findAction(Long id, String username) {
        return repo.findById(new CommentActionId(id, username)).orElse(null);
    }

    /**
     * Сохраняет или обновляет действие в базе данных.
     * <p>Аннотирован {@link Transactional} с
     * {@code readOnly = false}, так как изменяет данные.</p>
     *
     * @param action объект {@link CommentAction} для сохранения
     */
    @Transactional(readOnly = false)
    public void save(CommentAction action) {
        repo.save(action);
    }

    /**
     * Удаляет указанное действие из базы данных.
     * <p>Аннотирован {@link Transactional} с
     * {@code readOnly = false}, так как изменяет данные.</p>
     *
     * @param action объект {@link CommentAction} для удаления
     */
    @Transactional(readOnly = false)
    public void delete(CommentAction action) {
        repo.delete(action);
    }
}