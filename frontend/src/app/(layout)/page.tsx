"use client";

import "./globals.css";
import { Stack } from "react-bootstrap";
import { useEffect, useRef, useState } from "react";
import CreateFeed from "./components/feeds/CreateFeed";
import Feed from "./components/feeds/Feed";
import * as Types from "./utils/types";
import { useFeed } from "./contexts/FeedContext";
import { httpRequest } from "./utils/httpRequest";
import Loading from "./components/Loading";
import { useUser } from "./contexts/UserContext";
import { BACKEND_API_URL } from "./utils/globalValues";

export default function Home() {
  useEffect(() => {
    document.title = "DONT GO BACK"; // 크롬의 경우, 탭 이름까지 변경
  }, []);

  const [feedsState, setFeedsState] = useState<Types.Feed[]>([]);

  const [lastFeedId, setLastFeedId] = useState(0);
  const [feedsLoading, setFeedsLoading] = useState(false);
  const lastFeedIdRef = useRef(lastFeedId);
  const { userContext } = useUser();
  const [hasMoreFeeds, setHasMoreFeeds] = useState(true);

  useEffect(() => {
    //클로저
    lastFeedIdRef.current = lastFeedId;
  }, [lastFeedId]);
  // 피드 페이징
  const { feedContext, crudMyFeed, setCrudMyFeed } = useFeed();

  const fetchFeeds = async () => {
    if (feedsLoading) return <Loading />;
    if (!hasMoreFeeds) return;

    setFeedsLoading(true); // 로딩 시작
    const url = `${BACKEND_API_URL}/v1/feeds?lastFeedId=${lastFeedIdRef.current}&size=${10}`;
    const body = null;
    const success = async (result: Types.ResData<{ feeds: Types.Feed[] }>) => {
      setFeedsLoading(false);
      const newFeeds = result.data.feeds;
      if (newFeeds.length === 0) {
        setHasMoreFeeds(false);
        return;
      }
      setFeedsState((prevFeeds: Types.Feed[]) => [...prevFeeds, ...newFeeds]);
      setLastFeedId(newFeeds[newFeeds.length - 1].feedId);
    };
    const fail = () => {
      setFeedsLoading(false);
      console.error("피드 불러오기 실패");
      // alert("feed load fail");
    };
    httpRequest("GET", url, body, success, fail);
  };

  // 메인 피드에 필요한 데이터만 fetch
  useEffect(() => {
    const initData = async () => {
      if (lastFeedIdRef.current == 0 && hasMoreFeeds) {
        await fetchFeeds();
      }
    };
    initData();
  }, []);

  // 나의 피스 생성 반영 함수
  const createMyFeed = (createdFeed: Types.Feed) => {
    setFeedsState((prevFeeds) => [createdFeed, ...prevFeeds]);
  };
  // 나의 피드 수정 반영 함수
  const updateMyFeed = (updatedFeed: Types.Feed) => {
    setFeedsState((prevFeeds) =>
      prevFeeds.map((feed) => (feed.feedId === updatedFeed.feedId ? updatedFeed : feed))
    );
  };
  // 나의 피드 삭제 반영 함수
  const deleteMyFeed = (deletedFeed: Types.Feed) => {
    setFeedsState((prevFeeds) => prevFeeds.filter((feed) => feed.feedId !== deletedFeed.feedId));
  };

  // 자신 피드의 수정 삭제를 감지하여, 이를 반영
  useEffect(() => {
    // 생성
    if (crudMyFeed.C) {
      setCrudMyFeed({ ...crudMyFeed, C: false });
      if (feedContext) createMyFeed(feedContext);
      console.log(`[fID : ${feedContext?.feedId}] 게시물 생성 요청 감지`);
    }
    // 수정
    if (crudMyFeed.U) {
      setCrudMyFeed({ ...crudMyFeed, U: false });
      if (feedContext) updateMyFeed(feedContext);
      console.log(`[fID : ${feedContext?.feedId}] 게시물 수정 요청 감지`);
    }
    // 삭제
    if (crudMyFeed.D) {
      setCrudMyFeed({ ...crudMyFeed, D: false });
      if (feedContext) deleteMyFeed(feedContext);
      console.log(`[fID : ${feedContext?.feedId}] 게시물 삭제 요청 감지`);
    }
  }, [crudMyFeed]);

  // 스크롤을 감지하여 마지막에 다다르면 피드를 불러옴
  useEffect(() => {
    let timeoutId: NodeJS.Timeout;

    const handleScroll = () => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;

        if (scrollTop + clientHeight >= scrollHeight - 100 && !feedsLoading) {
          fetchFeeds();
        }
      }, 200); // 200ms 디바운스
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
      clearTimeout(timeoutId);
    };
  }, [feedsLoading, lastFeedId]);

  if (!userContext) return <Loading />;
  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">Dont Go Back</h5>

      <div className="pt-4 feeds-container">
        {/* 사이드바가 차지하지 않는 나머지 공간 */}
        <Stack gap={4} direction="vertical">
          {/* 글쓰기 영역 user기능 */}
          <div className="">
            <CreateFeed />
            <hr className="init mt-4 createFeedUnderLine" />
          </div>
          {feedsState.map((item, idx) => (
            <div key={idx}>
              <Feed feed={item} />
              <hr className="init mt-3 feedUnderLine" />
            </div>
          ))}
        </Stack>
      </div>
    </>
  );
}
