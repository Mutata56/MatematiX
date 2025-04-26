package mutata.com.github.MatematixProject.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Класс-композитный ключ для сущности {@link CommentAction}.
 * <p>Используется JPA для объединения полей <code>comment_id</code>
 * и <code>username</code> в составной первичный ключ.</p>
 *
 * <p>Реализует {@link Serializable} в соответствии с требованиями JPA
 * для идентификаторов-композитов.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see CommentAction
 */
@Data
public class CommentActionId implements Serializable {

    /**
     * Идентификатор комментария, часть составного ключа.
     */
    private Long comment_id;

    /**
     * Логин пользователя, часть составного ключа.
     */
    private String username;

    /**
     * Конструктор с параметрами для инициализации составного ключа.
     *
     * @param comment_id идентификатор комментария
     * @param username   логин пользователя
     */
    public CommentActionId(Long comment_id, String username) {
        this.comment_id = comment_id;
        this.username = username;
    }

    /**
     * Конструктор без параметров, необходимый для работы JPA.
     */
    public CommentActionId() {
    }
}
