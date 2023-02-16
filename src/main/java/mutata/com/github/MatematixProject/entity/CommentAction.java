package mutata.com.github.MatematixProject.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;


@Entity
@Table(name = "comment_user_actions")
@IdClass(CommentActionId.class)
@Data
public class CommentAction {

    @Id
    private Long comment_id;
    @Id
    private String username;

    private Byte action;

    public CommentAction(Long id, String username, byte action) {
        this.comment_id = id;
        this.username = username;
        this.action = action;
    }
    public CommentAction() {

    }
}

