package mutata.com.github.MatematixProject.entity.dto;

import lombok.Data;

/**
 * DTO - data transfer object - объект, используемый для передачи данных, в данном случае данных сущности комментария.
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 */
@Data
public class CommentDTO {
    /**
     * Имя пользователя, оставившего комментарий
     */
    private String username;
    /**
     * Рейтинг комментария
     */
    private long rating;
    /**
     * Дата, когда был оставлен комментарий
     */
    private long date;
    /**
     * Дата, когда был оставлен комментарий в виде строкового литерала
     */
    private String stringDate;
    /**
     * Содержимое комментария
     */
    private String content;
    /**
     * Id комментария
     */
    private long id;
    /**
     * Активен ли пользователь, оставивший комментарий
     */
    private boolean isActive;
    /**
     * Получатель комментария в строковом виде
     */
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
