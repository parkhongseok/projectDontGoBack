package com.dontgoback.dontgo.domain.article.controller;


import com.dontgoback.dontgo.domain.article.entity.Article;
import com.dontgoback.dontgo.domain.article.service.ArticleService;
import com.dontgoback.dontgo.global.rsData.RsData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
public class ApiV1ArticleController {
    private final ArticleService articleService;

    @Getter
    @AllArgsConstructor
    public static class ArticlesResponse {
        // 중첩객체를 선언하여, RsData<AList<Article>>의 복잡함 줄이기 DTO , 분리도 가능
        // 그니까 쉽게 말해서 AList<Article>라는 타입을 정의 한 것
        // 다시 말해 Tpye : articles를 정의한 것
        private final List<Article> articles;
    }

    @GetMapping("")
    public RsData<ArticlesResponse> getArticles() {
        List<Article> articles = this.articleService.getList();
        return RsData.of("S-200", "성공", new ArticlesResponse(articles));
    }

    // DTO : Data Transfer Object 분리도 가능.
    @Getter
    @AllArgsConstructor
    public static class ArticleResponse {
        private final Article article;
    }

    @GetMapping("/{id}")
    public RsData<ArticleResponse> getArticle(@PathVariable("id") Long id) {
        return articleService
            .getArticle(id)
            .map(article ->
                RsData.of(
                    "S-200",
                    "성공", new ArticleResponse(article)
            )).orElseGet(()->
                RsData.of(
                    "F-400",
                    "%d번 게시물이 존재하지 않습니다.".formatted(id),
                    null
        ));
    }
}
