'use client'

import "./globals.css";
import {Stack} from 'react-bootstrap';
import {useState } from 'react';
import CreateFeed from "./components/CreateFeed";
import Feed from "./components/Feed";
import Dummys from "./dummyData";
import * as Types from "./types";


export default function Home() {
  const user = Dummys.User;
  const [feeds] = useState<Types.Feed[]>(Dummys.Feeds);

  // useEffect(() => {
  //   fetch("http://localhost:8090/api/v1/feeds")
  //   .then(response=>response.json())
  //   .then((result)=>{
  //     console.log(result);
  //     setFeed(result.data.posts)
  //   })
  // }, [])

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4">Post</p>
      <div className="pt-4 feeds-container">
        {/* 사이드바가 차지하지 않는 나머지 공간 */}
        <Stack gap={4} direction="vertical" >
          {/* 글쓰기 영역 user기능 */}
          <div className="">

          <CreateFeed user = {user}/>
          <hr className="init mt-3"/>
          </div>
          {
            feeds.map((item, idx)=>
              <div key={idx} >
                <Feed feed = {item} />
                <hr className="init mt-3 fontGray1"/>
              </div>
          )
          }
        </Stack>
      </div>
    </>
  );
}
