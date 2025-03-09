//package com.dontgoback.dontgo.domain.feedLike;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/feedLikes")
//public class FeedLikeController {
//    private final FeedLikeService feedLikeService;
//
//    @GetMapping("/{id}")
//    public void Like(@PathVariable Long id){
//        feedLikeService.Like(id);
//    }
//}
