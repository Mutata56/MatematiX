package mutata.com.github.MatematixProject.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
/**
 * Класс, представляющий собой сущность, отображаемую в БД. В данном случае сущность комментарий.
 * Entity - Сущность, отображаемая в БД
 * Table - таблица в БД
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 * NoArgsConstructor - сказать lombok создавать конструктор без параметров
 */
@Table(name = "comments")
@Data
@Entity
@NoArgsConstructor
public class Comment {
    /**
     * Id - является id в таблице БД MySQL
     * GeneratedValue - инкреминтировать Id-шку для каждой сущности (новой)
     * Column - с какой колонкой в MySQL связть данное поле
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "content")
    private String content;
    /**
     * JsonIgnore - исключить определённое данное свойство объекта Java из сериализации и десериализации JSON.
     * Temporal - указание, что данное поле является типом времени
     */
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date date;

    /**
     * Transient - аннотация в Spring, которая указывает, что поле или метод не должны быть сохранены в базе данных
     */

    @Transient
    private String stringDate;

    @Column(name = "author")
    private String author;

    /**
     * Задача связи ManyToOne в контексте MySQL
     * JsonIgnore - исключить определённое данное свойство объекта Java из сериализации и десериализации JSON.
     */

    @ManyToOne
    @JoinColumn(name = "receiver")
    @JsonIgnore
    private User receiver;

    @Column(name = "rating")
    private long rating;




}
