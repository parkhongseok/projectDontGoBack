import * as Types from "./types";

const myText: string =
  "난 아직도 강아지 말 못 알아 듣는데 강아지는 내 말 다 알아 듣는다 강아지가 더 똑똑하다 강아지가 더 똑똑하다 강아지가 더 똑똑하다 강아지가 더 똑똑하다 아부러웡";

export const Dummys = {
  Feeds: [
    {
      feedId: 1,
      userId: 1,
      author: "FrontDummy",
      feedType: "BLUE",
      createdAt: "1시간전",
      content: myText,
      isLiked: false,
      likeCount: 0,
      commentCount: 0,
    },
    {
      feedId: 2,
      userId: 1,
      author: "FrontDummy2",
      feedType: "BLUE",
      createdAt: "1시간전",
      content: myText,
      isLiked: false,
      likeCount: 0,
      commentCount: 0,
    },
    {
      feedId: 3,
      userId: 1,
      author: "FrontDummy",
      feedType: "RED",
      createdAt: "1시간전",
      content: "contents Test",
      isLiked: true,
      likeCount: 30,
      commentCount: 0,
    },
  ] as Types.Feed[],

  Feed: {
    feedId: 0,
    userId: 0,
    author: "deleted",
    feedType: "deleted",
    createdAt: "deleted",
    content: "deleted",
    likeCount: 0,
    commentCount: 0,
  } as Types.Feed, // 단일 피드 예제

  Comments: [
    {
      commentId: 2,
      feedId: 2,
      userId: 1,
      author: "FrontDummy",
      commentType: "RED",
      createdAt: "1시간전",
      content: "실화냐?ㅋㅋ",
      likeCount: 4,
      subCommentCount: 0,
    },
    {
      commentId: 2,
      feedId: 2,
      userId: 1,
      author: "FrontDummy",
      commentType: "RED",
      createdAt: "1시간전",
      content: "실화냐?ㅋㅋ",
      likeCount: 4,
      subCommentCount: 0,
    },
    {
      commentId: 3,
      feedId: 3,
      userId: 2,
      author: "hihi",
      commentType: "BLUE",
      createdAt: "1시간전",
      content: "ㄹㅇㅋㅋ",
      likeCount: 2,
      subCommentCount: 0,
    },
  ] as Types.Comment[],

  Comment: {
    commentId: 0,
    feedId: 0,
    userId: 0,
    author: "deleted",
    commentType: "deleted",
    createdAt: "deleted",
    content: "deleted",
    likeCount: 0,
    subCommentCount: 0,
  } as Types.Comment, // 단일 댓글 예제

  User: {
    userId: 1,
    email: "helloworld@gmail.com",
    userName: "FrontDummy",
    userType: "BLUE",
  } as Types.User,
};

export default Dummys;
