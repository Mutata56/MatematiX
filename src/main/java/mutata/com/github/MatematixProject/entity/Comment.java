package mutata.com.github.MatematixProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * Сущность комментария в системе.
 * <p>Представляет собой комментарий пользователя к другому пользователю (receiver).
 * Хранит текст, дату создания, автора, получателя и рейтинг.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {

    /**
     * Уникальный идентификатор комментария.
     * <p>Автоматически генерируется базой данных.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    /**
     * Текстовое содержимое комментария.
     */
    @Column(name = "content")
    private String content;

    /**
     * Дата и время создания комментария.
     * <p>Не включается в JSON-ответы.</p>
     */
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date date;

    /**
     * Читаемое строковое представление даты.
     * <p>Не сохраняется в базе данных и используется для отображения.</p>
     */
    @Transient
    private String stringDate;

    /**
     * Автор комментария (логин пользователя).
     */
    @Column(name = "author")
    private String author;

    /**
     * Получатель комментария.
     * <p>Связь ManyToOne с таблицей пользователей. Не включается в JSON.</p>
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "receiver")
    @JsonIgnore
    private User receiver;

    /**
     * Текущий рейтинг комментария (число лайков минус дизлайки).
     */
    @Column(name = "rating")
    private int rating;
}