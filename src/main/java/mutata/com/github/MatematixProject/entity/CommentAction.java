
package mutata.com.github.MatematixProject.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * Сущность, представляющая действие пользователя над комментарием.
 * <p>Хранит информацию о том, поставил ли пользователь лайк или дизлайк к комментарию.</p>
 * <p>Использует составной первичный ключ из полей <code>comment_id</code> и <code>username</code>.</p>
 *
 * Таблица БД: <code>comment_user_actions</code>
 *<ul>
 *  <li><b>comment_id</b> — идентификатор комментария;</li>
 *  <li><b>username</b> — логин пользователя;</li>
 *  <li><b>action</b> — значение действия: -1 (нет), 0 (дизлайк), 1 (лайк).</li>
 *</ul>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see CommentActionId
 */
@Entity
@Table(name = "comment_user_actions")
@IdClass(CommentActionId.class)
@Data
public class CommentAction {

    /**
     * Идентификатор комментария, над которым выполняется действие.
     * Часть составного ключа.
     */
    @Id
    private Long comment_id;

    /**
     * Логин пользователя, который поставил лайк/дизлайк.
     * Часть составного ключа.
     */
    @Id
    private String username;

    /**
     * Тип действия пользователя над комментарием:
     * <ul>
     *   <li>-1 — действие не установлено;</li>
     *   <li> 0 — дизлайк;</li>
     *   <li> 1 — лайк;</li>
     * </ul>
     */
    private Byte action;

    /**
     * Конструктор для создания записи действия над комментарием.
     *
     * @param id        идентификатор комментария
     * @param username  логин пользователя, выполняющего действие
     * @param action    тип действия (-1, 0 или 1)
     */
    public CommentAction(Long id, String username, byte action) {
        this.comment_id = id;
        this.username = username;
        this.action = action;
    }

    /**
     * Конструктор без аргументов необходим для JPA.
     */
    public CommentAction() {
    }
}