'use client'

import "./globals.css";
import {Stack} from 'react-bootstrap';
import {useEffect, useState } from 'react';
import CreateFeed from "./components/CreateFeed";
import Feed from "./components/Feed";
import Dummys from "./utils/dummyData";
import * as Types from "./utils/types";
import { useFeed } from "./contexts/FeedContext";
import { httpRequest } from "./utils/httpRequest";
import { useUser } from "./contexts/UserContext";


export default function Home() {
  const [feeds, setFeeds] = useState<Types.Feed[]>(Dummys.Feeds);
  const { refreshMainFeeds } = useFeed();
  const { updateUserContext } = useUser();

  // 액세스 토큰을 URL에서 쿼리 파라미터로부터 추출하고 로컬 스토리지에 저장
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('access_token');
    if (token) {
      localStorage.setItem("access_token", token);
    }
  }, []);

  async function fetchFeeds() {
    const url = "http://localhost:8090/api/v1/feeds";
    const body = null; 
    const success = (result: any) => {      setFeeds(result.data.feeds);    };
    const fail = () => {      alert("feed load fail" )  };
    httpRequest("GET", url, body, success, fail);
  }

  useEffect(() => {
    updateUserContext();
    fetchFeeds();
  }, [refreshMainFeeds]);

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4">Post</p>
      <div className="pt-4 feeds-container">
        {/* 사이드바가 차지하지 않는 나머지 공간 */}
        <Stack gap={4} direction="vertical" >
          {/* 글쓰기 영역 user기능 */}
          <div className="">

          <CreateFeed/>
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
