package com.dontgoback.dontgo.domain.feed.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;



@Getter
@SuperBuilder
@AllArgsConstructor
public class CreateFeedResponse extends FeedTypeDto{
}
