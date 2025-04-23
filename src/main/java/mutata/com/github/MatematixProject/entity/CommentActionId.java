package mutata.com.github.MatematixProject.entity;

import lombok.Data;

import java.io.Serializable;
/**
 * Класс, представляющий собой сущность, ID для класса CommentAction. По сути нужен, чтобы соединить две сущности Id(comment_id и username) в одну (CommentActionId).
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 */
@Data
public class CommentActionId implements Serializable {
    private Long comment_id;

    private String username;

    public CommentActionId(Long comment_id,String username) {
        this.comment_id = comment_id;
        this.username = username;
    }
    public CommentActionId() {

    }
}