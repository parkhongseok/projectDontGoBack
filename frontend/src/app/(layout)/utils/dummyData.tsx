import * as Types from "./types";

const myText =
  "######################################### \n \n " +
  "Dont Go Back에 오신 것을 환영합니다. \n \n " +
  "이곳은 가진 돈이 유일한 이름인, 익명의 공간입니다. \n \n" +
  "#########################################  ";

export const Dummys = {
  // Feeds: [
  //   {
  //     feedId: 0,
  //     userId: 0,
  //     author: "관리자",
  //     userRole: "USER",
  //     feedType: "BLUE",
  //     createdAt: "지난 겨울",
  //     content: myText,
  //     isLiked: true,
  //     likeCount: 114,
  //     commentCount: 0,
  //   },
  // ] as Types.Feed[],

  Feed: {
    feedId: 0,
    userId: 0,
    content: "존재하지 않는 게시물입니다.",
    author: "Copybara",
    userRole: "USER",
    feedType: "BLUE",
    createdAt: "",
    updatedAt: "",
    likeCount: 0,
    commentCount: 0,
  } as Types.Feed, // 단일 피드 예제


  Comment: {
    commentId: 0,
    feedId: 0,
    userId: 0,
    author: "deleted",
    userRole: "USER",
    commentType: "BLUE",
    createdAt: "deleted",
    content: "deleted",
    likeCount: 0,
    subCommentCount: 0,
  } as Types.Comment, // 단일 댓글 예제

  // 가상 알림 데이터
  Notifications : [
  {
    id: 1,
    userImage: "https://placehold.co/40x40/EFEFEF/333?text=U1",
    userName: "카피바라",
    action: "님이 회원님의 게시물을 좋아합니다.",
    createdAt: "5분 전",
  },
  {
    id: 2,
    userImage: "https://placehold.co/40x40/E1E1E1/555?text=U2",
    userName: "아기바라",
    action: "님이 회원님을 팔로우하기 시작했습니다.",
    createdAt: "1시간 전",
  },
  {
    id: 3,
    userImage: "https://placehold.co/40x40/DEDEDE/777?text=U3",
    userName: "왕바라",
    action: '님이 댓글을 남겼습니다: "정말 멋져요!"',
    createdAt: "3시간 전",
  },
] as Types.Notification[],

  // Comments: [
  //   {
  //     commentId: 2,
  //     feedId: 2,
  //     userId: 1,
  //     author: "FrontDummy",
  //     commentType: "RED",
  //     createdAt: "1시간전",
  //     content: "실화냐?ㅋㅋ",
  //     likeCount: 4,
  //     subCommentCount: 0,
  //   },
  //   {
  //     commentId: 2,
  //     feedId: 2,
  //     userId: 1,
  //     author: "FrontDummy",
  //     commentType: "RED",
  //     createdAt: "1시간전",
  //     content: "실화냐?ㅋㅋ",
  //     likeCount: 4,
  //     subCommentCount: 0,
  //   },
  //   {
  //     commentId: 3,
  //     feedId: 3,
  //     userId: 2,
  //     author: "hihi",
  //     commentType: "BLUE",
  //     createdAt: "1시간전",
  //     content: "ㄹㅇㅋㅋ",
  //     likeCount: 2,
  //     subCommentCount: 0,
  //   },
  // ] as Types.Comment[],

  // User: {
  //   userId: 1,
  //   email: "helloworld@gmail.com",
  //   userName: "FrontDummy",
  //   userType: "BLUE",
  //   userRole: "USER",
  //   profileVisibility: "PUBLIC"
  // } as Types.User,
};

export default Dummys;
