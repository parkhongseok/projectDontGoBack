"use client";

import "./globals.css";
import { Stack } from "react-bootstrap";
import { useCallback, useEffect, useRef, useState } from "react";
import CreateFeed from "./components/CreateFeed";
import Feed from "./components/Feed";
import * as Types from "./utils/types";
import { useFeed } from "./contexts/FeedContext";
import { httpRequest } from "./utils/httpRequest";
import { useUser } from "./contexts/UserContext";

export default function Home() {
  const [feeds, setFeeds] = useState<Types.Feed[]>([]);
  const [lastFeedId, setLastFeedId] = useState(0);
  const [feedLoading, setFeedLoading] = useState(false);
  const lastFeedIdRef = useRef(lastFeedId);
  useEffect(() => {
    //클로저
    lastFeedIdRef.current = lastFeedId;
  }, [lastFeedId]);
  const { feedContext, crudMyFeed, setCrudMyFeed } = useFeed();
  const { userContext, fetchUserContext } = useUser();
  const fetchFeeds = () => {
    if (feedLoading) return;
    setFeedLoading(true); // 로딩 시작
    const url = `http://localhost:8090/api/v1/feeds?lastFeedId=${lastFeedIdRef.current}&size=${10}`;
    const body = null;
    const success = async (result: any) => {
      if (result.data.feeds.length === 0) return;
      await setFeeds((prevFeeds: Types.Feed[]) => [...prevFeeds, ...result.data.feeds]);
      await setLastFeedId(result.data.feeds[result.data.feeds.length - 1].feedId);
      setFeedLoading(false);
    };
    const fail = () => {
      console.error("피드 불러오기 실패");
      setFeedLoading(false);
      // alert("feed load fail");
    };
    httpRequest("GET", url, body, success, fail);
  };

  // 액세스 토큰을 URL에서 쿼리 파라미터로부터 추출하고 로컬 스토리지에 저장
  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get("access_token");
    if (token) {
      localStorage.setItem("access_token", token);
    }
  }, []);

  // 메인 피드에 필요한 데이터만 fetch
  useEffect(() => {
    const fetchUserAndFeeds = async () => {
      if (!userContext?.userId) await fetchUserContext();
      if (lastFeedIdRef.current == 0) {
        await fetchFeeds();
      }
    };
    fetchUserAndFeeds();
  }, []);

  // 나의 피드 수정 반영 함수
  const updateMyFeed = (updatedFeed: Types.Feed) => {
    setFeeds((prevFeeds) => prevFeeds.map((feed) => (feed.feedId === updatedFeed.feedId ? updatedFeed : feed)));
  };

  // 나의 피드 삭제 반영 함수
  const deleteMyFeed = (deletedFeed: Types.Feed) => {
    setFeeds((prevFeeds) => prevFeeds.filter((feed) => feed.feedId !== deletedFeed.feedId));
  };

  const createMyFeed = (createdFeed: Types.Feed) => {
    setFeeds((prevFeeds) => [createdFeed, ...prevFeeds]);
  };

  // 자신 피드의 수정 삭제를 감지하여, 이를 반영
  useEffect(() => {
    if (crudMyFeed.U) {
      // 수정 작업
      if (feedContext) updateMyFeed(feedContext);
      console.log(feedContext);
      console.log(`[fID : ${feedContext?.feedId}] 게시물 수정 요청 감지`);
      setCrudMyFeed({ ...crudMyFeed, U: false }); // 수정 후 상태 초기화
      // 서버 요청으로 피드 수정 API 호출
    }
    if (crudMyFeed.D) {
      console.log(feedContext);
      // 삭제 작업
      if (feedContext) {
        deleteMyFeed(feedContext);
        console.log(`[fID : ${feedContext?.feedId}] 게시물 삭제 요청 감지`);
        setCrudMyFeed({ ...crudMyFeed, D: false }); // 삭제 후 상태 초기화
      }
      // 서버 요청으로 피드 삭제 API 호출
    }
    if (crudMyFeed.C) {
      // 생성 작업
      if (feedContext) createMyFeed(feedContext);
      console.log(`[fID : ${feedContext?.feedId}] 게시물 생성 요청 감지`);
      setCrudMyFeed({ ...crudMyFeed, C: false }); // 생성 후 상태 초기화
      // 서버 요청으로 피드 생성 API 호출
    }
  }, [crudMyFeed]);

  // 스크롤을 감지하여 마지막에 다다르면 피드를 불러옴
  useEffect(() => {
    let timeoutId: NodeJS.Timeout;

    const handleScroll = () => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;

        if (scrollTop + clientHeight >= scrollHeight - 100 && !feedLoading) {
          fetchFeeds();
        }
      }, 200); // 200ms 디바운스
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
      clearTimeout(timeoutId);
    };
  }, [feedLoading, lastFeedId]);

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4">Post</p>
      <div className="pt-4 feeds-container">
        {/* 사이드바가 차지하지 않는 나머지 공간 */}
        <Stack gap={4} direction="vertical">
          {/* 글쓰기 영역 user기능 */}
          <div className="">
            <CreateFeed />
            <hr className="init mt-3" />
          </div>
          {feeds.map((item, idx) => (
            <div key={idx}>
              <Feed feed={item} />
              <hr className="init mt-3 fontGray1" />
            </div>
          ))}
        </Stack>
      </div>
    </>
  );
}
