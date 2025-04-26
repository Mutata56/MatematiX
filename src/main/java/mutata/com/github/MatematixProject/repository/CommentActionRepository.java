
package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.CommentAction;
import mutata.com.github.MatematixProject.entity.CommentActionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для управления действиями пользователей над комментариями.
 * <p>Наследует {@link JpaRepository}, что автоматически предоставляет
 * стандартные CRUD-операции и методы поиска по составному ключу {@link CommentActionId}.</p>
 *
 * Работает с сущностью {@link CommentAction}, содержащей информацию о лайках
 * и дизлайках пользователей по комментариям.
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see CommentAction
 * @see CommentActionId
 * @see JpaRepository
 */
@Repository
public interface CommentActionRepository extends JpaRepository<CommentAction, CommentActionId> {
    // Дополнительные методы выборки можно определить при необходимости,
    // например: List<CommentAction> findByCommentId(Long commentId);
}