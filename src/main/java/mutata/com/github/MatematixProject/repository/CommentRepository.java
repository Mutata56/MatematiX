package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Репозиторий в контексте Spring, который автоматически создаёт методы для работы с соотв. БД (исходя из названия репозитория)
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    /**
     * Кастомный метод репозитория дла поиска комментариев по пользователю
     * @param receiver - пользователь, у которого нужно найти комментарии
     * @return список найденных комментариев
     */
    List<Comment> findCommentsByReceiver(User receiver);
    /**
     * Кастомный метод репозитория дла поиска комментариев по пользователю, сортировка по дате
     * @param receiver - пользователь, у которого нужно найти комментарии
     * @param pageRequest - сущность страницы с соотв. результатами (Нужна для пагинации)
     * @return Сущность страницы, заполненная комментариями и соотв. отсортированная
     */
    Page<Comment> findCommentsByReceiverOrderByDateDesc(User receiver, PageRequest pageRequest);

}
