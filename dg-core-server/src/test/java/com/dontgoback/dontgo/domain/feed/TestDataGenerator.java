//package com.dontgoback.dontgo.domain.feed;
//
//import com.dontgoback.dontgo.domain.comment.CommentService;
//import com.dontgoback.dontgo.domain.feedLike.FeedLike;
//import com.dontgoback.dontgo.domain.feedLike.FeedLikeService;
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.domain.user.UserService;
//import com.dontgoback.dontgo.domain.userSetting.AccountStatusHistoryService;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.RedBlueType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class TestDataGenerator {
//    private final UserService userService;
//    private final FeedService feedService;
//    private final FeedLikeService feedLikeService;
//    private final CommentService commentService;
//    private final AccountStatusHistoryService accountStatusHistoryService;
//
//    public void generate(int userMax, int feedMax, int likesPerFeed, int commentsPerFeed) {
//        List<User> users = new ArrayList<>();
//        for (int i = 0; i < userMax; i++) {
//            User user = userService.createDummyUser(
//                    (i % 2 == 0 ? "%d00만원" : "-%d00만원").formatted(i + 1),
//                    "user%d@email.com".formatted(i),
//                    (i % 2 == 0 ? RedBlueType.BLUE : RedBlueType.RED)
//            );
//            users.add(user);
//            accountStatusHistoryService.initializeStatus(user, AccountStatus.ACTIVE, "상태 초기화");
//        }
//
//        List<Feed> feeds = new ArrayList<>();
//        for (int i = 0; i < feedMax; i++) {
//            Feed feed = feedService.createDummyFeed(users.get(i % userMax), "%d번 피드입니다.".formatted(i + 1));
//            feeds.add(feed);
//        }
//
//        for (Feed feed : feeds) {
//            for (int j = 0; j < likesPerFeed; j++) {
//                User liker = users.get((feed.getId().intValue() + j) % userMax);
//                feedLikeService.saveFeedLike(new FeedLike(new FeedLikeId(liker.getId(), feed.getId())));
//            }
//
//            for (int k = 0; k < commentsPerFeed; k++) {
//                if (feed.getId() % 2 == 0) {
//                    User commenter = users.get((feed.getId().intValue() + k) % userMax);
//                    commentService.createDummyComment(feed, commenter,
//                            "%d번 피드에 %d번 유저의 댓글입니다.".formatted(feed.getId(), commenter.getId()));
//                }
//            }
//        }
//    }
//}
