package mutata.com.github.service;

import mutata.com.github.entity.Comment;
import mutata.com.github.entity.User;
import mutata.com.github.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository repository;

    @Autowired
    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    public void save(Comment comment) {
        repository.save(comment);
    }

    public List<Comment> findByReceiver(User user) {
        return repository.findCommentsByReceiver(user);
    }

}
