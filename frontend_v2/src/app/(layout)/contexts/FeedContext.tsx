import React, { createContext, useContext, useEffect, useState } from "react";
import * as Types from "../utils/types";

// FeedContext와 제공할 값 정의
interface FeedContextType {
  feedContext: Types.Feed | null;
  setFeedContext: (feed: Types.Feed) => void;
  updateFeedContext: (updatedFeed: Types.Feed) => void;
  refreshMainFeeds: boolean;
  setRefreshMainFeeds: (refrashFeed: boolean) => void;
  crudMyFeed: { C: boolean; R: boolean; U: boolean; D: boolean };
  setCrudMyFeed: React.Dispatch<React.SetStateAction<{ C: boolean; R: boolean; U: boolean; D: boolean }>>;
}

// FeedContext 생성
const FeedContext = createContext<FeedContextType | undefined>(undefined);

// FeedProvider 컴포넌트로 상태 관리 제공
export const FeedProvider = ({ children }: { children: React.ReactNode }) => {
  const [feedContext, setFeedContext] = useState<Types.Feed | null>(null);

  // 전역으로 사용할 의존성 리스트 변경 인자
  const [refreshMainFeeds, setRefreshMainFeeds] = useState(false);

  // 전역으로 사용할 의존성 리스트 변경 인자
  const [crudMyFeed, setCrudMyFeed] = useState({ C: false, R: false, U: false, D: false });

  // feedContext 업데이트 함수 수정
  const updateFeedContext = (newFeed: Types.Feed) => {
    setFeedContext((prevFeedContext) => {
      const updatedFeedContext = { ...prevFeedContext, ...newFeed };
      localStorage.setItem("feedContext", JSON.stringify(updatedFeedContext));
      return updatedFeedContext;
    });
  };

  // 로컬스토리지에서 데이터 복구
  // useEffect(() => {
  //   const savedFeed = localStorage.getItem("feedContext");
  //   if (savedFeed) {
  //     updateFeedContext(JSON.parse(savedFeed));
  //   }
  //   // setIsLoaded(true); // 복구 완료
  // }, []);

  return <FeedContext.Provider value={{ feedContext, setFeedContext, updateFeedContext, refreshMainFeeds, setRefreshMainFeeds, crudMyFeed, setCrudMyFeed }}>{children}</FeedContext.Provider>;
};

// FeedContext를 쉽게 사용할 수 있도록 커스텀 훅
export const useFeed = () => {
  const context = useContext(FeedContext);
  if (!context) {
    throw new Error("useFeeds must be used within a FeedProvider");
  }
  return context;
};
