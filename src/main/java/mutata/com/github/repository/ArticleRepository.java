package mutata.com.github.repository;

import mutata.com.github.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long> {


}
