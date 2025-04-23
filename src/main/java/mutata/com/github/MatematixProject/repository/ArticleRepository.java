package mutata.com.github.MatematixProject.repository;

import mutata.com.github.MatematixProject.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий в контексте Spring, который автоматически создаёт методы для работы с соотв. БД (исходя из названия репозитория)
 */
public interface ArticleRepository extends JpaRepository<Article,Long> {


}
