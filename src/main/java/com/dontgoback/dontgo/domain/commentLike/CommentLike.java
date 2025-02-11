//package com.dontgoback.dontgo.domain.commentLike;
//
//import com.dontgoback.dontgo.domain.comment.Comment;
//import com.dontgoback.dontgo.domain.user.User;
//import com.dontgoback.dontgo.global.jpa.EmbeddedTypes.CommentLikeId;
//import jakarta.persistence.*;
//import lombok.*;
//
//@Entity
//@Getter
//@Setter
//@ToString
//@AllArgsConstructor
//@NoArgsConstructor
//public class CommentLike {
//
//    @EmbeddedId
//    private CommentLikeId id;
//
//    @MapsId("userId")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;
//
//    @MapsId("commentId")
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
//    private Comment comment;
//}