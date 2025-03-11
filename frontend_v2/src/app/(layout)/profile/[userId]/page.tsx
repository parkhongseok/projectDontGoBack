"use client";

import { Stack, Tab, Tabs } from "react-bootstrap";
import "../../globals.css";
import CreateFeed from "../../components/CreateFeed";
import Feed from "../../components/Feed";
import * as Types from "../../utils/types";
import { useCallback, useEffect, useRef, useState } from "react";
import Profile from "../../components/profiles/Profile";
import styles from "../../components/Feed.module.css";
import Chart from "../../components/profiles/Chart";
import Footer from "../../components/Footer";
import { httpRequest } from "../../utils/httpRequest";
import { useParams } from "next/navigation";
import Loading from "../../components/Loading";
import { useUser } from "../../contexts/UserContext";
import { useFeed } from "../../contexts/FeedContext";
import { BACKEND_API_URL } from "../../utils/values";

export default function ProfileMain() {
  const { userId } = useParams<{ userId: string }>();
  const [feedsState, setFeedsState] = useState<Types.Feed[]>([]);
  const [userState, setUserState] = useState<Types.User>();

  const [redFeedsState, setRedFeedsState] = useState<Types.Feed[]>([]);
  const [blueFeedsState, setBlueFeedsState] = useState<Types.Feed[]>([]);
  const [feedsLoading, setFeedsLoading] = useState(false);
  const [lastFeedId, setLastFeedId] = useState(0);
  const lastFeedIdRef = useRef(lastFeedId);
  const { userContext } = useUser();
  const { feedContext, crudMyFeed, setCrudMyFeed } = useFeed();

  //클로저
  useEffect(() => {
    lastFeedIdRef.current = lastFeedId;
  }, [lastFeedId]);

  const fetchUser = useCallback(async () => {
    const url = `${BACKEND_API_URL}/v1/users/${userId}`;
    const body = null;
    const success = (result: Types.ResData<Types.User>) => {
      setUserState(result.data);
    };
    const fail = () => {};
    httpRequest("GET", url, body, success, fail);
  }, [userId]);

  const fetchFeeds = useCallback(async () => {
    if (feedsLoading) return <Loading />;
    setFeedsLoading(true); // 로딩 시작
    const url = `${BACKEND_API_URL}/v1/feeds/profile?userId=${userId}&lastFeedId=${
      lastFeedIdRef.current
    }&size=${10}`;
    const body = null;
    const success = async (result: Types.ResData<{ feeds: Types.Feed[] }>) => {
      setFeedsLoading(false);
      const newFeeds = result.data.feeds;
      if (newFeeds.length === 0) return;
      setFeedsState((prevFeeds: Types.Feed[]) => [...prevFeeds, ...newFeeds]);
      setLastFeedId(newFeeds[newFeeds.length - 1].feedId);
    };
    const fail = () => {
      setFeedsLoading(false);
      console.error("피드 불러오기 실패");
      // alert("feed load fail");
    };
    httpRequest("GET", url, body, success, fail);
  }, [feedsLoading, userId]);

  // 메인 피드에 필요한 데이터만 fetch
  useEffect(() => {
    const initData = async () => {
      if (!userState?.userId) await fetchUser();
      if (lastFeedIdRef.current == 0) {
        await fetchFeeds();
      }
    };
    initData();
  }, [userState, fetchUser, fetchFeeds]);

  useEffect(() => {
    setRedFeedsState(feedsState.filter((feed: Types.Feed) => feed.feedType === "RED"));
    setBlueFeedsState(feedsState.filter((feed: Types.Feed) => feed.feedType === "BLUE"));
  }, [feedsState]);

  // 프로필에 실시간 생성 및 수정 반영
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
      if (feedContext) {
        console.log(`[fID : ${feedContext.feedId}] 게시물 생성 요청 감지`);
        createMyFeed(feedContext);
        if (feedsState.length == 0) setLastFeedId(feedContext?.feedId); // 이부분이 없으면 첫번째 댓글 조회 시, 동일 내용 추가
      }
    }
    // 수정
    if (crudMyFeed.U) {
      setCrudMyFeed({ ...crudMyFeed, U: false });
      if (feedContext) {
        console.log(`[fID : ${feedContext.feedId}] 게시물 수정 요청 감지`);
        updateMyFeed(feedContext);
      }
    }
    // 삭제
    if (crudMyFeed.D) {
      setCrudMyFeed({ ...crudMyFeed, D: false });
      if (feedContext) {
        console.log(`[fID : ${feedContext?.feedId}] 게시물 삭제 요청 감지`);
        deleteMyFeed(feedContext);
      }
    }
  }, [crudMyFeed, feedContext, feedsState.length, setCrudMyFeed]);

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
  }, [feedsLoading, fetchFeeds, lastFeedId]);

  if (!userState) return <Loading />;
  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">Profile</h5>

      <div className="pt-4 feeds-container">
        {/* 사이드바가 차지하지 않는 나머지 공간 */}

        <Profile userProps={userState} />
        <Chart />
        <Tabs
          defaultActiveKey="Main"
          id="uncontrolled-tab-example"
          variant="underline"
          className="mb-3"
          justify
        >
          <Tab eventKey="Main" title="Main" className={`${styles.tabs} `}>
            <hr className="init mb-4 feedUnderLine" />
            <Stack gap={4} direction="vertical">
              {/* 글쓰기 영역 user기능 */}
              {userContext?.userId == userState.userId && (
                <div className="">
                  <CreateFeed />
                  <hr className="init mt-4 createFeedUnderLine" />
                </div>
              )}
              {feedsState.map((item: Types.Feed, idx: number) => (
                <div key={idx}>
                  <Feed feed={item} />
                  <hr className="init mt-3 feedUnderLine" />
                </div>
              ))}
            </Stack>
          </Tab>
          <Tab eventKey="Red" title="Red">
            <hr className="init mb-4 feedUnderLine" />
            <Stack gap={4} direction="vertical">
              {redFeedsState.map((item: Types.Feed, idx: number) => (
                <div key={idx}>
                  <Feed feed={item} />
                  <hr className="init mt-3 feedUnderLine" />
                </div>
              ))}
            </Stack>
          </Tab>
          <Tab eventKey="Blue" title="Blue">
            <hr className="init mb-4 feedUnderLine" />
            <Stack gap={4} direction="vertical">
              {blueFeedsState.map((item: Types.Feed, idx: number) => (
                <div key={idx}>
                  <Feed feed={item} />
                  <hr className="init mt-3 feedUnderLine" />
                </div>
              ))}
            </Stack>
          </Tab>
        </Tabs>
        <Footer />
      </div>
    </>
  );
}
