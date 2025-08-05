import * as Types from "./types";

const myText =
  "######################################### \n \n " +
  "Dont Go Back에 오신 것을 환영합니다. \n \n " +
  "이곳은 가진 돈이 유일한 이름인, 익명의 공간입니다. \n \n" +
  "#########################################  ";

export const Dummys = {
  Feeds: [
    {
      feedId: 0,
      userId: 0,
      author: "관리자",
      feedType: "BLUE",
      createdAt: "지난 겨울",
      content: myText,
      isLiked: true,
      likeCount: 114,
      commentCount: 0,
    },
  ] as Types.Feed[],

  DeletedFeed: {
    feedId: 0,
    userId: 0,
    author: "Copybara",
    feedType: "",
    createdAt: "",
    content: "존재하지 않는 게시물입니다.",
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
