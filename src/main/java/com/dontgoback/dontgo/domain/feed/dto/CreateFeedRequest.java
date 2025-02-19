package com.dontgoback.dontgo.domain.feed.dto;

//import com.dontgoback.dontgo.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateFeedRequest {
    private String content;
}
