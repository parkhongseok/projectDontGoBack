package com.dontgoback.dontgo.domain.feed.controller;

import com.dontgoback.dontgo.domain.feed.dto.PostResponse;
import com.dontgoback.dontgo.domain.feed.dto.PostsResponse;
import com.dontgoback.dontgo.domain.feed.entity.Post;
import com.dontgoback.dontgo.domain.feed.service.PostService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class ApiV2FeedController {
    private final FeedService feedService;

    @GetMapping("")
    public ResData<PostsResponse> getPosts(){
        List<Post> posts= this.postService.getPostAll();
        return ResData.of("S-200", "성공", new PostsResponse(posts));
    }

    @GetMapping("/{id}")
    public ResData<PostResponse> getPostOne(@PathVariable("id") Long id){
        return postService
                .getPostOne(id)
                .map(post ->
                        ResData.of(
                                "S-200",
                                "성공",
                                new PostResponse(post)
                        ))
                .orElseGet(()->
                        ResData.of(
                            "S-200",
                            "실패",
                            null
                ));
    }

}
