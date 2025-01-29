package com.dontgoback.dontgo.domain.feed.service;
import com.dontgoback.dontgo.domain.feed.entity.Post;
import com.dontgoback.dontgo.domain.feed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public List<Post>  getPostAll(){
        return postRepository.findAll();
    }

    public Optional<Post> getPostOne(long id) {
        return postRepository.findById(id);

    }

    public void createDummyPost(String content){
        Post post = Post.builder()
                .content(content)
                .build();
        this.postRepository.save(post);
    }



}
