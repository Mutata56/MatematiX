
package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления комментариями пользователей.
 * <p>Обеспечивает сохранение, поиск и пагинацию
 * сущностей {@link Comment} через {@link CommentRepository}.</p>
 * <p>Аннотация {@link Transactional} с параметром
 * {@code readOnly = true} указывает, что методы класса
 * по умолчанию выполняются только для чтения.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see Comment
 * @see CommentRepository
 */
@Service
@Transactional(readOnly = true)
public class CommentService {

    /**
     * Репозиторий для операций над сущностью {@link Comment}.
     */
    private final CommentRepository repository;

    /**
     * Конструктор для внедрения зависимости репозитория.
     *
     * @param repository репозиторий комментариев
     */
    @Autowired
    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    /**
     * Сохраняет новый или обновлённый комментарий в базе данных.
     * <p>Аннотирован {@link Transactional}</p>
     *
     * @param comment объект {@link Comment} для сохранения
     */
    @Transactional(readOnly = false)
    public void save(Comment comment) {
        repository.save(comment);
    }

    /**
     * Получает список всех комментариев, адресованных указанному пользователю.
     *
     * @param user получатель комментариев
     * @return список комментариев, полученных пользователем
     */
    public List<Comment> findByReceiver(User user) {
        return repository.findCommentsByReceiver(user);
    }

    /**
     * Возвращает страницу комментариев, отсортированных по дате убывания,
     * для указанного получателя.
     *
     * @param user          получатель комментариев
     * @param page          номер страницы для пагинации (0-based)
     * @param itemsPerPage  число элементов на странице
     * @return объект {@link Page} с комментариями и метаданными пагинации
     */
    public Page<Comment> findAllReturnPage(User user, int page, int itemsPerPage) {
        return repository.findCommentsByReceiverOrderByDateDesc(
                user, PageRequest.of(page, itemsPerPage)
        );
    }

    /**
     * Ищет комментарий по его идентификатору.
     *
     * @param id идентификатор комментария
     * @return объект {@link Comment} или {@code null}, если не найден
     */
    public Comment findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
