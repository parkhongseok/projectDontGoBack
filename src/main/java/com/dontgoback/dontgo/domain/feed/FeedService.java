package com.dontgoback.dontgo.domain.feed;
import com.dontgoback.dontgo.domain.feed.dto.*;
import com.dontgoback.dontgo.domain.user.User;
import com.dontgoback.dontgo.global.resData.ResData;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    // 다건 조회
    public FeedsResponse getFeedsResponse(){
        return new FeedsResponse(feedRepository.findFeedsResponse());
    }
    // 단건 조회
    public Optional<FeedResponse> getFeedResponse(long id) {
        return feedRepository.findFeedResponseById(id);
    }

    // 피드 생성
    @Transactional
    public ResData<CreateFeedResponse> createFeed(User user, CreateFeedRequest feedRequest) {
       Feed feed = Feed.builder()
               .user(user)
               .content(feedRequest.getContent())
               .feedType(user.getUserType())
               .build();
       this.feedRepository.save(feed);
       // 위 부분을 try catch로 감싸서, 각각의 비어있는 필드에 대한 에러 반환도 가능
       return ResData.of(
               "S-3",
               "게시물이 생성되었습니다.",
               CreateFeedResponse.builder().
                       feedId(feed.getId())
                       .feedType(feed.getFeedType())
                       .content(feed.getContent())
                       .build()
       );
    }

    @Transactional
    public void createDummyFeed(User user, String content) {
        Feed feed = Feed.builder()
                .user(user)
                .content(content)
                .feedType(user.getUserType())
                .build();
        this.feedRepository.save(feed);
    }

    public Optional<Feed> findById(Long id){
        return feedRepository.findById(id);
    }

    // 수정
    @Transactional
    public ResData<UpdateFeedResponse> updateFeed(Long id, UpdateFeedRequest updateFeedRequest) {
        Optional<Feed> optionalFeed = findById(id);
        if (optionalFeed.isPresent()){
            Feed feed = optionalFeed.get();
            authorizeFeedUser(feed); // try catch 로 작성자랑 다른 경우 다른 행동 가능, 코드 설정 필요
            // 유저 권한 확인 과정 추가
            feed.setContent(updateFeedRequest.getContent());
            feedRepository.save(feed);

            return ResData.of(
                    "S-4",
                    "%d번 게시글이 수정되었습니다.".formatted(id),
                    UpdateFeedResponse.builder()
                            .feedId(feed.getId())
                            .content(feed.getContent())
                            .feedType(feed.getFeedType())
                            .build()
            );
        }
        return ResData.of("F-4",
                "%d번 게시글을 찾을 수 없습니다.".formatted(id),
                null);
    }

    // 삭제
    public ResData<DeleteFeedResponse> deleteById(Long id) {
        Optional<Feed> optionalFeed = findById(id);
        if (optionalFeed.isPresent()){
            Feed feed = optionalFeed.get();
            authorizeFeedUser(feed);
            DeleteFeedResponse deleteFeedResponse = DeleteFeedResponse.builder()
                    .feedId(feed.getId())
                    .feedType(feed.getFeedType())
                    .content(feed.getContent())
                    .build();
            feedRepository.delete(feed);
            return ResData.of(
                        "S-2",
                        "%d번 게시글이 삭제되었습니다.".formatted(id),
                        deleteFeedResponse
                );
        }
        return ResData.of(
                "F-4",
                "%d번 게시글이 존재하지 않습니다..".formatted(id),
                null
        );
    }

    // 피드를 작성한 게 본인인지 확인
    private static void authorizeFeedUser(Feed feed){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!feed.getUser().getUsername().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }

}
