package com.dontgoback.dontgo.domain.article.entity;


import com.dontgoback.dontgo.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Article extends BaseEntity {
        private String title;
        private String content;
//        private String author;
//        private String date;
//        private String category;
//        private String image;
//        private String url;
}
