package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findCommentsByReceiver(User receiver);
    Page<Comment> findCommentsByReceiverOrderByDateDesc(User receiver, PageRequest pageRequest);

}
