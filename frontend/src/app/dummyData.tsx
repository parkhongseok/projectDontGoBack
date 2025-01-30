
import Types from './types';

namespace Dummys {
  const myText : string = "난 아직도 강아지 말 못 알아 듣는데 강아지는 내 말 다 알아 듣는다 강아지가 더 똑똑하다 강아지가 더 똑똑하다 강아지가 더 똑똑하다 강아지가 더 똑똑하다 아부러웡";
  
  export const Feeds : Types.Feed[] = [
    {feedId:1, userId: 1, userName: "before", feedType : "red", beforeTime:"1시간전", content: myText, likeCount: 0, commentCount : 0 },
    {feedId:2, userId: 1, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 30, commentCount : 0 },
    {feedId:3, userId: 2, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 40, commentCount : 0 },
    {feedId:4, userId: 2, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 40, commentCount : 0 },
    {feedId:5, userId: 4, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 20, commentCount : 0 },
    {feedId:6, userId: 3, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 10, commentCount : 0 },
    {feedId:7, userId: 5, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 230, commentCount : 0 },
    {feedId:6, userId: 3, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 10, commentCount : 0 },
    {feedId:7, userId: 5, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 230, commentCount : 0 },
    {feedId:6, userId: 3, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 10, commentCount : 0 },
    {feedId:7, userId: 5, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 230, commentCount : 0 }
  ];

  export const Feed : Types.Feed = Feeds[0];


  export const Comments : Types.Comment[] = [
    {commentId:1, feedId:1, userId: 1, userName: "before", commentType : "red", beforeTime:"1시간전", content: myText, likeCount: 1, commentCount : 2 },
    {commentId:2, feedId:2, userId: 1, userName: "before", commentType : "red", beforeTime:"1시간전", content: "실화냐?ㅋㅋ ", likeCount: 4, commentCount : 0 },
    {commentId:3, feedId:3, userId: 2, userName: "hihi", commentType : "blue", beforeTime:"1시간전", content: "ㄹㅇㅋㅋ", likeCount: 2, commentCount : 0 },
    {commentId:4, feedId:4, userId: 2, userName: "before", commentType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 4, commentCount : 0 },
    {commentId:5, feedId:5, userId: 4, userName: "hihi", commentType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 4, commentCount : 0 },
    {commentId:6, feedId:6, userId: 3, userName: "before", commentType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 1, commentCount : 0 },
    {commentId:7, feedId:7, userId: 5, userName: "hihi", commentType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 2, commentCount : 0 }
  ];
  
  export const Comment : Types.Comment = Comments[1]

  export const User : Types.User = {
    userId: 1,
    email : "helloworld@gmail.com",
    profileVisibility : "public",
    userName: "1200만원 (나)",
    userType: "blue"
  }
}

export default Dummys;