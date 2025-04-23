package mutata.com.github.MatematixProject.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Класс, представляющий собой сущность, отображаемую в БД. В данном случае сущность "действие над комментарием".
 * Entity - Сущность, отображаемая в БД
 * Table - таблица в БД
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 */

@Entity
@Table(name = "comment_user_actions")
@IdClass(CommentActionId.class)
@Data
public class CommentAction {
    /**
     * Id в данном случае формируется по двум полям (колонкам в MySQL) объекта
     */
    @Id
    private Long comment_id;
    /**
     * Юзернейм пользователя, который совершает действие над комментарием
     */
    @Id
    private String username;
    /**
     * Возможные значения action:
     * -1 не установлен
     * 0 дизлайк
     * 1 лайк
     */
    private Byte action;

    public CommentAction(Long id, String username, byte action) {
        this.comment_id = id;
        this.username = username;
        this.action = action;
    }
    public CommentAction() {

    }
}

