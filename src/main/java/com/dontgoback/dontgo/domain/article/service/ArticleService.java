package com.dontgoback.dontgo.domain.article.service;

import com.dontgoback.dontgo.domain.article.entity.Article;
import com.dontgoback.dontgo.domain.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;

    public List<Article> getList() {
        return articleRepository.findAll();
    }

    public Optional<Article> getArticle(Long id) {
        return this.articleRepository.findById(id);

    }

    public void create(String title, String content) {
        Article article = Article.builder()
                .title(title)
                .content(content)
                .build();
        this.articleRepository.save(article);
    }
}
