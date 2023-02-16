package mutata.com.github.MatematixProject.entity.dto;

import lombok.Data;


@Data
public class CommentDTO {
    private String username;
    private long rating;
    private long date;
    private String stringDate;
    private String content;
    private long id;
    private boolean isActive;
    private String recipient;

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    public void setIsActive(boolean isActive) { // JACKSON
        this.isActive = isActive;
    }
    public boolean getIsActive() {
        return isActive;
    }


}
