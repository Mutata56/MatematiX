package mutata.com.github.MatematixProject.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Сущность, представляющая статью на сайте.
 * <p>Каждая статья хранится в таблице <code>articles</code> в базе данных,
 * имеет уникальный идентификатор и имя для отображения и поиска.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
public class Article {

    /**
     * Уникальный идентификатор статьи.
     * <p>Автоматически генерируется базой данных при вставке новой записи.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * Читаемое название статьи.
     * <p>Используется для отображения в списках и при отображении конкретной статьи.</p>
     */
    @Column(name = "name")
    private String name;
}