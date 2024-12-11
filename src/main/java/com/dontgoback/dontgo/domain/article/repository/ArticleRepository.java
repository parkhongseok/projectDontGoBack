package com.dontgoback.dontgo.domain.article.repository;
import com.dontgoback.dontgo.domain.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
