package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для управления сущностями {@link Comment}.
 * <p>Наследует {@link JpaRepository}, что предоставляет стандартные
 * CRUD-операции и дополнительные возможности JPA для работы
 * с комментариями пользователей.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see Comment
 * @see JpaRepository
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Извлекает все комментарии, адресованные указанному пользователю.
     * <p>Возвращает список {@link Comment}, связанных с получателем.</p>
     *
     * @param receiver пользователь, для которого выбираются комментарии
     * @return список комментариев, получателю которых соответствует указанный пользователь
     */
    List<Comment> findCommentsByReceiver(User receiver);

    /**
     * Извлекает комментарии, адресованные указанному пользователю,
     * с постраничной пагинацией и сортировкой по дате в порядке убывания.
     * <p>Используется для отображения последних комментариев первыми.</p>
     *
     * @param receiver    пользователь, для которого выбираются комментарии
     * @param pageRequest объект {@link PageRequest}, задающий параметры страницы (номер и размер)
     * @return объект {@link Page} с комментариями, отсортированными по убыванию даты
     */
    Page<Comment> findCommentsByReceiverOrderByDateDesc(User receiver, PageRequest pageRequest);
}