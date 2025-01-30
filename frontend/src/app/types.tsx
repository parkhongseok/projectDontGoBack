namespace Types {

  // Post 컴포넌트에도 중복되는 코드
  export type Feed = {
    feedId: number;
    userId: number;
    userName: string; //join
    feedType: string;
    beforeTime: string;
    content: string;
    likeCount: number;
    commentCount: number;
  };
  
  export type User = {
    userId : number;
    email : string;
    profileVisibility : string;
    userName : string; //join
    userType : string; //join
    
  }

  export type Comment ={
    commentId : number;
    feedId : number;
    userId : number;
    userName : string; //join
    commentType : string;
    beforeTime : string;
    content : string;
    likeCount : number;
    commentCount : number;
  }
}
  export default Types;