package com.dontgoback.dontgo.domain.post.controller;

import com.dontgoback.dontgo.domain.post.dto.PostsResponse;
import com.dontgoback.dontgo.domain.post.entity.Post;
import com.dontgoback.dontgo.domain.post.service.PostService;
import com.dontgoback.dontgo.global.resData.ResData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {
    private final PostService postService;

    @GetMapping("")
    public ResData<PostsResponse> getPosts(){
        List<Post> posts = postService.getPostAll();
        return ResData.of("S-200", "성공", new PostsResponse(posts));
    }

    @GetMapping("/{id}")
    public Optional<Post> getPostOne(@PathVariable("id") Long id){
    return postService.getPostOne(id);
    }

}
