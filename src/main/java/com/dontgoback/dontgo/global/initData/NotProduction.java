package com.dontgoback.dontgo.global.initData;

import com.dontgoback.dontgo.domain.article.service.ArticleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev") // dev, test 환경에서만 사용
public class NotProduction {
    @Bean
    CommandLineRunner initData(ArticleService articleService) {
        return (args) -> {
            articleService.create("제목1", "내용1");
            articleService.create("제목2", "내용2");
            articleService.create("제목3", "내용3");
            articleService.create("제목4", "내용4");
            articleService.create("제목5", "내용5");
        };
    }
}
