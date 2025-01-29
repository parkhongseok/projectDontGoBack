'use client'

import "./globals.css";
import {Stack} from 'react-bootstrap';
import { useEffect, useState } from 'react';
import CreateFeed from "./components/CreateFeed";
import Feed from "./components/Feed";
import CreateBox from "./components/CreateBox";


// Post 컴포넌트에도 중복되는 코드
type TypeOfFeed = {
  userId: number;
  userName: string;
  feedType: string;
  beforeTime: string;
  content: string;
  likeCount: number;
  commentCount: number;
};

export default function Home() {
  const [feeds, setFeed] = useState<TypeOfFeed[]>([
    {userId: 1, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "before", feedType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "hihi", feedType : "blue", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 }
  ]);

  const userInfo = {
    userId: 1,
    userName: "영희",
    feedType: "blue"
  }

  // useEffect(() => {
  //   fetch("http://localhost:8090/api/v1/feeds")
  //   .then(response=>response.json())
  //   .then((result)=>{
  //     console.log(result);
  //     setPost(result.data.posts)
  //   })
  // }, [])

  const [showWriteBox, setShowWriteBox] = useState(false);



  return (
    <div className="main-layout">
      <div className="sidebar-space"></div>

      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3">Post</p>
        <div className="pt-4 feeds-container">
          {/* 사이드바가 차지하지 않는 나머지 공간 */}
          <Stack gap={4} direction="vertical" >
            {/* 글쓰기 영역 user기능 */}
            <div className="">
              {
              showWriteBox ? <CreateBox user = {userInfo} setShowWriteBox = {setShowWriteBox}/> : null
              }
            <CreateFeed 
            user = {userInfo}
            setShowWriteBox = {setShowWriteBox}
            />
            <hr className="feed-underline"/>
            </div>
            {
              feeds.map((item, idx)=>
                <div key={idx}>
                  <Feed feed = {item} ></Feed>
                  <hr className="feed-underline"/>
                </div>
            )
            }
          </Stack>
        </div>
      </div>

    </div>
  );
}
