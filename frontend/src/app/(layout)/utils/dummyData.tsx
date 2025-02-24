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
      likeCount: 0,
      commentCount: 0,
    },
    {
      feedId: 2,
      userId: 1,
      author: "FrontDummy",
      feedType: "RED",
      createdAt: "1시간전",
      content: "contents Test",
      likeCount: 30,
      commentCount: 0,
    },
    // { feedId: 3, userId: 2, userName: "hihi", feedType: "BLUE", createdAt: "1시간전", content: "contents Test", likeCount: 40, commentCount: 0 },
    // { feedId: 4, userId: 2, userName: "hihi", feedType: "RED", createdAt: "1시간전", content: "contents Test", likeCount: 40, commentCount: 0 },
    // { feedId: 5, userId: 4, userName: "wait", feedType: "BLUE", createdAt: "1시간전", content: "contents Test", likeCount: 20, commentCount: 0 },
    // { feedId: 6, userId: 3, userName: "before", feedType: "RED", createdAt: "1시간전", content: "contents Test", likeCount: 10, commentCount: 0 },
    // { feedId: 7, userId: 5, userName: "wiwiwi", feedType: "BLUE", createdAt: "1시간전", content: "contents Test", likeCount: 230, commentCount: 0 }
  ] as Types.Feed[],

  Feed: {} as Types.Feed, // 단일 피드 예제

  Comments: [
    {
      commentId: 1,
      feedId: 1,
      userId: 1,
      author: "FrontDummy",
      commentType: "RED",
      createdAt: "1시간전",
      content: myText,
      likeCount: 1,
      subCommentCount: 2,
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

  Comment: {} as Types.Comment, // 단일 댓글 예제

  User: {
    userId: 1,
    email: "helloworld@gmail.com",
    userName: "FrontDummy",
    userType: "BLUE",
  } as Types.User,
};

export default Dummys;
