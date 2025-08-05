package com.dontgoback.dontgo.domain.feed;

import com.dontgoback.dontgo.domain.feed.dto.*;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.resData.ResData;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    // 피드를 작성한 게 본인인지 확인
    private static void authorizeFeedUser(Feed feed) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!feed.getUser().getUsername().equals(userName)) {
            // 403 Forbidden
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한 없음 : 본인이 작성한 게시물이 아닙니다.");
        }
    }

    // 다건 조회
    public FeedsResponse getFeedsResponse(Long lastFeedId, int size, Long currentUserId) {
        return new FeedsResponse(feedRepository.findFeedsResponse(lastFeedId, size, currentUserId));
    }

    // 다건 조회
    public FeedsResponse getProfileFeedsResponse(Long targetUserId, Long lastFeedId, int size, Long currentUserId) {
        return new FeedsResponse(feedRepository.findProfileFeedsResponse(targetUserId, lastFeedId, size, currentUserId));
    }


    // 단건 조회
    public FeedResponse getFeedResponse(Long id, Long currentUserId) {
        return feedRepository.findFeedResponseById(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 피드입니다."));
    }

    // 피드 등록
    @Transactional
    public CreateFeedResponse createFeed(User user, CreateFeedRequest feedRequest) {
        // 유저 권한 확인 과정 추가 : 이를테면 유저의 Type, 또는 아직 userAsset이 설정되지 않은 사람 등
        Feed feed;
        try {
            feed = Feed.builder()
                    .user(user)
                    .content(feedRequest.getContent())
                    .author(user.getUserAsset())
                    .feedType(user.getUserType())
                    .build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "[게시물 등록 실패] 유저 정보 부족");
        }

        saveFeed(feed); // 저장 실패 시 400 반환

        return CreateFeedResponse.builder()
                .feedId(feed.getId())
                .userId(user.getId())
                .content(feed.getContent())
                .author(feed.getAuthor())
                .feedType(feed.getFeedType())
                .createdAt(feed.getCreatedAt())
                .updatedAt(feed.getUpdatedAt())
                .build();
    }

    // 수정
    @Transactional
    public UpdateFeedResponse updateFeed(Long id, UpdateFeedRequest updateFeedRequest) {
        Feed feed = findById(id); // 실패 시  404 NOT_FOUND 반환
        authorizeFeedUser(feed); // 실패 시 403 Forbidden 반환
        // 유저 권한 확인 과정 추가 : 이를테면 유저의 Type, 또는 아직 userAsset이 설정되지 않은 사람 등
        feed.setContent(updateFeedRequest.getContent());
        feed.preUpdate(); // 수동으로 설정 (이거 없으면 저장 후 영속성 컨텍스트에 없어서, 아직 DB 반영 전 객체 정보만 불러와짐

        return UpdateFeedResponse.builder()
                .feedId(feed.getId())
                .content(feed.getContent())
                .updatedAt(feed.getUpdatedAt()) // LastModifiedDate에서 자동 설정됨
                .build();
    }

    // 소프트 삭제
    @Transactional
    public DeleteFeedResponse softDeleteById(Long id) {
        Feed feed = findById(id); // 피드가 있는지 검사
        authorizeFeedUser(feed);  // 해당 피드를 지울 자격이 있는지 검사
        if (feed.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 삭제된 게시물입니다.");
        }
        feed.setDeletedAt(LocalDateTime.now());

        return new DeleteFeedResponse(feed.getId());
    }
    // 저장
    public void saveFeed(Feed feed) {
        try {
            feedRepository.save(feed);
        } catch (Exception e) {
            // 400
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "게시물 저장 실패 : 잘못된 요청 데이터입니다.");
        }
    }
    // 삭제된 피드인지 확인
    public boolean isFeedDeleted(Feed feed){
        return feed.getDeletedAt() != null;
    }

    @Transactional
    public Feed createDummyFeed(User user, String content) {
        Feed feed = Feed.builder()
                .user(user)
                .content(content)
                .author(user.getUserAsset())
                .feedType(user.getUserType())
                .build();
        this.feedRepository.save(feed);
        return feed;
    }

    public Feed findById(Long id) {
        return feedRepository.findById(id)
                // 404
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "%d번 게시글을 찾을 수 없습니다.".formatted(id)));
    }


}
