package mutata.com.github.repository;

import mutata.com.github.entity.Comment;
import mutata.com.github.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findCommentsByReceiver(User receiver);
}
