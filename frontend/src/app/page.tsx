'use client'

import Post from "./components/Post";
import "./globals.css";
import {Stack} from 'react-bootstrap';
import { useEffect, useState } from 'react';

// Post 컴포넌트에도 중복되는 코드
type TypeOfPost = {
  userId: number;
  userName: string;
  postType: string;
  beforeTime: string;
  content: string;
  likeCount: number;
  commentCount: number;
};

export default function Home() {
  const [posts, setPost] = useState<TypeOfPost[]>([
    {userId: 1, userName: "before", postType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "before", postType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 },
    {userId: 1, userName: "before", postType : "red", beforeTime:"1시간전", content: "contents Test ", likeCount: 0, commentCount : 0 }
  ]);

  useEffect(() => {
    fetch("http://localhost:8090/api/v1/posts")
    .then(response=>response.json())
    .then((result)=>{
      console.log(result);
      setPost(result.data.posts)
    })
  }, [])

  return (
    <div className="main-layout">
      <div className="sidebar-space"></div>

      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3">Post</p>
        <div className="pt-4 posts-container">
          {/* 사이드바가 차지하지 않는 나머지 공간 */}
          <Stack gap={4} direction="vertical" >
            {/* <div className="pt-4"> 글쓰기 영역 </div> */}
            {
              posts.map((item, idx)=>
                <div key={idx} className="postOne">
                  <Post postOne = {item} ></Post>
                  <hr className="post-underline"/>
                </div>
            )
            }
          </Stack>
        </div>
      </div>

    </div>
  );
}
