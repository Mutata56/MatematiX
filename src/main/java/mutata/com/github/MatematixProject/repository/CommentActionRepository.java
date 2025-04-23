package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.CommentAction;
import mutata.com.github.MatematixProject.entity.CommentActionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Репозиторий в контексте Spring, который автоматически создаёт методы для работы с соотв. БД (исходя из названия репозитория)
 */
@Repository
public interface CommentActionRepository extends JpaRepository<CommentAction, CommentActionId> {

}
