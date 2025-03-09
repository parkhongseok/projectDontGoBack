package com.dontgoback.dontgo.domain.feedLike;

import com.dontgoback.dontgo.domain.feed.Feed;
import com.dontgoback.dontgo.domain.feedLike.dto.FeedLikeResponse;
import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.FeedLikeId;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedLikeService {
    private final FeedLikeRepository feedLikeRepository;

    @Transactional
    public FeedLikeResponse toggleFeedLike(Long userId, Long feedId) {

        FeedLikeId id = new FeedLikeId(userId, feedId);
        Optional<FeedLike> optionalFeedLike = feedLikeRepository.findById(id);
        if (optionalFeedLike.isPresent()){
            // 이미 좋아요를 누른 경우 -> 좋아요 취소
            FeedLike feedLike = optionalFeedLike.get();
            feedLikeRepository.delete(feedLike);
            return new FeedLikeResponse(false, countByFeedId(feedId));
        } else {
            // 좋아요 추가
            FeedLike feedLike = new FeedLike(id);
            feedLikeRepository.save(feedLike);
            return new FeedLikeResponse(true, countByFeedId(feedId));
        }
    }

    public int countByFeedId(Long feedId){
       return feedLikeRepository.countByFeedId(feedId);
    }


    // for Dummydata
    @Transactional
    public void saveFeedLike(FeedLike feedLike){
        //이처럼 순수 DAO 역할 메서드를 이중으로 두는게 안전한가
        // 추후 복잡해지는 경우 FeedLikePersistenceService 로 옮기기 고려,
        // + private메서드인 경우, 프록시 거치지 않아서 트렌젝션에 안전하지 않을 수도
        try {
            feedLikeRepository.save(feedLike);
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "좋아요 실패 : 잘못된 요청 데이터입니다.");
        }
    }

}
