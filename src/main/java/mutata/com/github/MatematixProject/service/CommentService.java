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
/**
 * Сервис, связанный с комментариями.
 * @see Comment
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository repository;

    @Autowired
    public CommentService(CommentRepository repository) {
        this.repository = repository;
    }

    /**
     * Сохранение действия в БД
     * @param comment - комментарий, который нужно сохранить в БД
     * Transactional(readOnly = false) - данный метод занимается изменением БД
     */
    @Transactional(readOnly = false)
    public void save(Comment comment) {
        repository.save(comment);
    }

    /**
     * Найти получателя комментария по Юзеру
     */

    public List<Comment> findByReceiver(User user) {
        return repository.findCommentsByReceiver(user);
    }

    /**
     * Найти все комментарии юзера на странице page, сделать пагинацию, itemsPerPage единиц на страницу
     */

    public Page<Comment> findAllReturnPage(User user,int page,int itemsPerPage) {
      return repository.findCommentsByReceiverOrderByDateDesc(user,PageRequest.of(page,itemsPerPage));
    }

    /**
     * Найти получателя комментария по Id
     */

    public Comment findById(Long id) {
        return repository.findById(id).orElse(null);
    }
}
