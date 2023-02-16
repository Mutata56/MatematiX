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

    public Page<Comment> findAllReturnPage(User user,int page,int itemsPerPage) {
      return repository.findCommentsByReceiverOrderByDateDesc(user,PageRequest.of(page,itemsPerPage));
    }

    public Comment findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
