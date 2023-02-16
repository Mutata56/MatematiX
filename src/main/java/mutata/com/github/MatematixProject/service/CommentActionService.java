package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.CommentAction;
import mutata.com.github.MatematixProject.entity.CommentActionId;
import mutata.com.github.MatematixProject.repository.CommentActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CommentActionService {

    private final CommentActionRepository repo;

    @Autowired
    public CommentActionService(CommentActionRepository repo) {
        this.repo = repo;
    }

    public CommentAction findAction(Long id,String username) {
        return repo.findById(new CommentActionId(id,username)).orElse(null);
    }

    @Transactional(readOnly = false)
    public void save(CommentAction action) {
        repo.save(action);
    }

    @Transactional(readOnly = false)
    public void delete(CommentAction action) {
        repo.delete(action);
    }
}
