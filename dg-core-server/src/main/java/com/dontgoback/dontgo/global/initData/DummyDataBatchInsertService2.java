//package com.dontgoback.dontgo.global.initData;
//
//import com.dontgoback.dontgo.domain.accountStateHistory.AccountStatusHistoryService;
//import com.dontgoback.dontgo.domain.assetHistory.AssetHistoryService;
//import com.dontgoback.dontgo.domain.comment.Comment;
//import com.dontgoback.dontgo.domain.feed.Feed;
//import com.dontgoback.dontgo.domain.feedLike.FeedLike;
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.AccountStatus;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
//import jakarta.persistence.EntityManager;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Profile("dev")
//@RequiredArgsConstructor
//public class DummyDataBatchInsertService2 {
//    private final EntityManager em;
//    private final AccountStatusHistoryService accountStatusHistoryService;
//    private final AssetHistoryService assetHistoryService;
//
//    /** 한 번만 실행되는 더미 데이터 삽입 */
//    @Transactional
//    public void insertDummyData() {
//
//        /* ===================== 1. 파라미터 ===================== */
//        int writerCount      = 10;   // 메인 유저
//        int feedPerWriter    = 10;      // 10 000 feeds
//        int commentPoolSize  = 10;     // 댓글 작성 전용 유저
//        int likePoolSize     = 10;     // 좋아요 전용 유저
//        int batchSize        = 500;     // flush 단위
//        /* ======================================================= */
//
//        long insertsSinceFlush = 0L;
//
//        /* ---------- 2. 댓글·좋아요 전용 유저 풀 생성 ---------- */
//        List<User> commenterPool = new ArrayList<>(commentPoolSize);
//        List<User> likerPool     = new ArrayList<>(likePoolSize);
//
//        for (int i = 0; i < commentPoolSize; i++) {
//            User c = makeUser("commenter", i);
//            em.persist(c);
//            accountStatusHistoryService.initializeStatus(c, AccountStatus.ACTIVE, "commenter");
//            commenterPool.add(c);
//            insertsSinceFlush += 2;
//        }
//        for (int i = 0; i < likePoolSize; i++) {
//            User l = makeUser("liker", i);
//            em.persist(l);
//            accountStatusHistoryService.initializeStatus(l, AccountStatus.ACTIVE, "liker");
//            likerPool.add(l);
//            insertsSinceFlush += 2;
//        }
//
//        /* ---------- 3. writer·feed·댓글·좋아요 생성 ---------- */
//        int commentIndex = 0;  // 풀에서 순차 사용
//        int likeIndex    = 0;
//
//        for (int w = 0; w < writerCount; w++) {
//
//            /* 3-1. writer 유저 */
//            User writer = makeUser("writer", w);
//            em.persist(writer);
//            accountStatusHistoryService.initializeStatus(writer, AccountStatus.ACTIVE, "writer");
//            insertsSinceFlush += 2;
//
//            /* 3-2. 피드 10개 */
//            for (int f = 0; f < feedPerWriter; f++) {
//
//                Feed feed = Feed.builder()
//                        .user(writer)
//                        .author(writer.getUserAsset())
//                        .feedType(writer.getUserType())
//                        .content("테스트 피드입니다.")
//                        .build();
//                em.persist(feed);
//                insertsSinceFlush++;
//
//                /* 3-3. 댓글: 피드 1 000 개당 1 개 */
//                if ((w * feedPerWriter + f) % 2 == 0 && commentIndex < commentPoolSize) {
//                    User commenter = commenterPool.get(commentIndex++);
//                    Comment cmt = Comment.builder()
//                            .feed(feed)
//                            .user(commenter)
//                            .author(commenter.getUserAsset())
//                            .commentType(commenter.getUserType())
//                            .content("테스트 댓글입니다.")
//                            .build();
//                    em.persist(cmt);
//                    insertsSinceFlush++;
//                }
//
//                /* 3-4. 좋아요: 피드 100 개당 1 개 */
//                if ((w * feedPerWriter + f) % 2 == 0 && likeIndex < likePoolSize) {
//                    User liker = likerPool.get(likeIndex++);
//                    em.persist(new FeedLike(new FeedLikeId(liker.getId(), feed.getId())));
//                    insertsSinceFlush++;
//                }
//
//                /* 3-5. flush & clear */
//                if (insertsSinceFlush >= batchSize) {
//                    em.flush();
//                    em.clear();
//                    insertsSinceFlush = 0;
//                }
//            }
//        }
//
//        /* ---------- 4. 잔여 flush ---------- */
//        if (insertsSinceFlush > 0) {
//            em.flush();
//            em.clear();
//        }
//    }
//    /* ---------- 유틸: 고유 이메일을 보장하는 User 빌더 ---------- */
//    private User makeUser(String prefix, int seq) {
//        return User.builder()
//                .email(prefix + seq + "@example.com")
//                .build();
//    }
//
//    /* ---------- 유틸: 고유 이메일을 보장하는 User 빌더 ---------- */
//    private void makeAccount(String email, int seq) {
//        User user = makeUser(email, seq);
//        em.persist(user);
//        accountStatusHistoryService.initializeStatus(user, AccountStatus.ACTIVE, "email");
//        em.persist(user);
//        assetHistoryService.createHistory(user, 100_000);
//
//    }
//
//}
