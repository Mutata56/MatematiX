package mutata.com.github.MatematixProject.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Класс, представляющий собой сущность, отображаемую в Бд. В данном случае сущность статья.
 * Entity - Сущность, отображаемая в БД
 * Table - таблица в БД
 * Data - это сокращенная аннотация, сочетающая возможности @ToString, @EqualsAndHashCode, @Getter @Setter и @RequiredArgsConstructor
 * NoArgsConstructor - сказать lombok создавать конструктор без параметров
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "articles")
public class Article {
    /**
     * Id - является id в таблице БД MySQL
     * GeneratedValue - инкреминтировать Id-шку для каждой сущности (новой)
     * Column - с какой колонкой в MySQL связть данное поле
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;
}
