package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.CommentAction;
import mutata.com.github.MatematixProject.entity.CommentActionId;
import mutata.com.github.MatematixProject.repository.CommentActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис, связанный с действиями надо комментариями.
 * @see CommentAction
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */
@Service
@Transactional(readOnly = true)
public class CommentActionService {

    private final CommentActionRepository repo;

    @Autowired
    public CommentActionService(CommentActionRepository repo) {
        this.repo = repo;
    }

    /**
     * Поиск действия по id и юзернейму
     * @param id - id комментария
     * @param username - юзернейм пользовтеля
     */

    public CommentAction findAction(Long id,String username) {
        return repo.findById(new CommentActionId(id,username)).orElse(null);
    }

    /**
     * Сохранения действия в БД
     * @param action - действие, которое нужно сохранить в БД
     * Transactional(readOnly = false) - данный метод занимается изменением БД
     */

    @Transactional(readOnly = false)
    public void save(CommentAction action) {
        repo.save(action);
    }

    /**
     * Удаления действия из БД
     * @param action - действие, которое нужно удалить из БД
     * Transactional(readOnly = false) - данный метод занимается изменением БД
     */

    @Transactional(readOnly = false)
    public void delete(CommentAction action) {
        repo.delete(action);
    }
}
