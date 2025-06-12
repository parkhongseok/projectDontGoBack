//package com.dontgoback.dontgo.global.initData;
//
//
//import com.dontgoback.dontgo.domain.comment.Comment;
//import com.dontgoback.dontgo.domain.comment.CommentService;
//import com.dontgoback.dontgo.domain.feed.Feed;
//import com.dontgoback.dontgo.domain.feed.FeedService;
//import com.dontgoback.dontgo.domain.feedLike.FeedLike;
//import com.dontgoback.dontgo.domain.feedLike.FeedLikeService;
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.domain.user.UserService;
//import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistoryService;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Configuration
//@Profile("dev")// dev, test 환경에서만 사용
//@RequiredArgsConstructor
//public class DummyDataBatchInsert {
//
//    private final DummyDataBatchInsertService insertService;
//
//    @Bean
//    public CommandLineRunner runInsert() {
//        return args -> {
//            insertService.insertDummyData();  // 트랜잭션 내에서 안전하게 실행됨
//        };
//    }
//}
