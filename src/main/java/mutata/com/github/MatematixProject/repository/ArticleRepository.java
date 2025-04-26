
package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с сущностью {@link Article}.
 * <p>Наследует {@link JpaRepository}, предоставляя стандартные
 * CRUD-операции и возможность создания запросов по имени методов.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see JpaRepository
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Дополнительные методы для выборки статей можно объявить здесь,
    // например:
    // List<Article> findByNameContaining(String keyword);
}