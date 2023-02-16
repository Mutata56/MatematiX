package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.CommentAction;
import mutata.com.github.MatematixProject.entity.CommentActionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentActionRepository extends JpaRepository<CommentAction, CommentActionId> {

}
