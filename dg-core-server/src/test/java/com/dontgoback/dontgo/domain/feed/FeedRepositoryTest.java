//package com.dontgoback.dontgo.domain.feed;
//
//import com.dontgoback.dontgo.domain.feed.dto.FeedResponse;
//import jakarta.persistence.EntityManager;
//import org.hibernate.stat.Statistics;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.util.List;
//
//@SpringBootTest
//@ActiveProfiles("dev") // test DB 및 설정 사용
//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // static 없이 @BeforeAll 허용
//public class FeedRepositoryTest {
//    @Autowired FeedRepository feedRepository;
//    @Autowired EntityManager entityManager;
//    @Autowired TestDataGenerator testDataGenerator;
//
//    @BeforeAll
//    void setUp() {
//        testDataGenerator.generate(10, 100, 30, 10);
//    }
//
//    @Test
//    void compareQueryPerformance() {
//        Statistics stats = entityManager.getEntityManagerFactory()
//                .unwrap(org.hibernate.engine.spi.SessionFactoryImplementor.class)
//                .getStatistics();
//        stats.setStatisticsEnabled(true);
//
//        // 기존 쿼리
//        stats.clear();
//        long startA = System.currentTimeMillis();
//        List<FeedResponse> resultA = feedRepository.findFeedsResponse(0L, 20, 1L);
//        long endA = System.currentTimeMillis();
//        System.out.println("기존 쿼리 시간: " + (endA - startA) + "ms");
//        System.out.println("기존 쿼리 수: " + stats.getQueryExecutionCount());
//
//        // 개선 쿼리
//        stats.clear();
//        long startB = System.currentTimeMillis();
//        List<FeedResponse> resultB = feedRepository.findFeedsResponseOptimized(0L, 20, 1L);
//        long endB = System.currentTimeMillis();
//        System.out.println("개선 쿼리 시간: " + (endB - startB) + "ms");
//        System.out.println("개선 쿼리 수: " + stats.getQueryExecutionCount());
//    }
//}
