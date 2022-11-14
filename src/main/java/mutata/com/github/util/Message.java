package mutata.com.github.util;

import lombok.Data;
import java.util.Date;

@Data
public class Message {
    private String avatar;
    private String username;
    private long rating;
    private String date;
    private String content;
    private String format;
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
