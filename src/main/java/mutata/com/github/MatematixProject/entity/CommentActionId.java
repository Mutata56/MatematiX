package mutata.com.github.MatematixProject.entity;

import lombok.Data;

import java.io.Serializable;

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