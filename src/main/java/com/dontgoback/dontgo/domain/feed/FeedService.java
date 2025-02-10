package com.dontgoback.dontgo.domain.feed.service;
import com.dontgoback.dontgo.domain.feed.entity.Feed;
import com.dontgoback.dontgo.domain.feed.repository.FeedRepository;
import com.dontgoback.dontgo.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;

    public List<Feed>  getFeeds(){
        return feedRepository.findAll();
    }

    public Optional<Feed> getFeed(long id) {
        return feedRepository.findById(id);
    }

    public Optional<User> getUser(long id){

    }

    public void createDummyFeed(long userId, String content, String feedType){
       Feed feed = Feed.builder()
               .userId(userId)
               .content(content)
               .feedType(feedType)
               .build();
        this.feedRepository.save(feed);
    }
}
