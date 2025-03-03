"use client";

import { Stack, Tab, Tabs } from "react-bootstrap";
import "../../globals.css";
import CreateFeed from "../../components/CreateFeed";
import Feed from "../../components/Feed";
import * as Types from "../../utils/types";
import { useEffect, useRef, useState } from "react";
import Profile from "../../components/profiles/Profile";
import styles from "../../components/Feed.module.css";
import Chart from "../../components/profiles/Chart";
import Footer from "../../components/Footer";
import { httpRequest } from "../../utils/httpRequest";
import { useParams } from "next/navigation";
import Loading from "../../components/Loading";
import ProfileSetting from "../../components/profiles/ProfileSetting";

export default function ProfileMain() {
  const { userId } = useParams<{ userId: string }>();
  const [feedsState, setFeedsState] = useState<Types.Feed[]>([]);
  const [userState, setUserState] = useState<Types.User>();

  const [redFeedsState, setRedFeedsState] = useState(feedsState);
  const [blueFeedsState, setBlueFeedsState] = useState(feedsState);
  const [feedsLoading, setFeedsLoading] = useState(false);
  const [lastFeedId, setLastFeedId] = useState(0);
  const lastFeedIdRef = useRef(lastFeedId);


  //클로저
  useEffect(() => {
    lastFeedIdRef.current = lastFeedId;
  }, [lastFeedId]);

  const fetchUser = async () => {
    const url = `http://localhost:8090/api/v1/users/${userId}`;
    const body = null;
    const success = (result: any) => {
      setUserState(result.data);
    };
    const fail = () => {};
    httpRequest("GET", url, body, success, fail);
  };

  const fetchFeeds = async () => {
    if (feedsLoading) return <Loading />;
    setFeedsLoading(true); // 로딩 시작
    const url = `http://localhost:8090/api/v1/feeds/profile?userId=${userId}&lastFeedId=${
      lastFeedIdRef.current
    }&size=${10}`;
    const body = null;
    const success = async (result: any) => {
      setFeedsLoading(false);
      let newFeeds = result.data.feeds;
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
  };

  // 메인 피드에 필요한 데이터만 fetch
  useEffect(() => {
    const initData = async () => {
      if (!userState?.userId) await fetchUser();
      if (lastFeedIdRef.current == 0) {
        await fetchFeeds();
      }
    };
    initData();
  }, [userState]);

  useEffect(() => {
    setRedFeedsState((prevFeeds) => prevFeeds.filter((feed) => feed.feedType !== "BLUE"));
    setBlueFeedsState((prevFeeds) => prevFeeds.filter((feed) => feed.feedType !== "RED"));
  }, [feedsState]);

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

  if (!userState) return <Loading />;
  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">Profile</h5>

      <div className="pt-4 feeds-container">
        {/* 사이드바가 차지하지 않는 나머지 공간 */}

        <Profile user={userState} />
        <Chart />
        <Tabs
          defaultActiveKey="Main"
          id="uncontrolled-tab-example"
          variant="underline"
          className="mb-3"
          justify
        >
          <Tab eventKey="Main" title="Main" className={`${styles.tabs} `}>
            <Stack gap={4} direction="vertical">
              {/* 글쓰기 영역 user기능 */}
              <div className="mt-1">
                <CreateFeed />
                <hr className="init mt-4 createFeedUnderLine" />
              </div>
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
function userState(Feeds: import("../../utils/types").Feed[]): [any, any] {
  throw new Error("Function not implemented.");
}
