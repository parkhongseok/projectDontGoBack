import React, { createContext, useContext, useEffect, useState} from 'react';
import Types from "../types"

// FeedContext와 제공할 값 정의
interface FeedContextType {
  feedContext: Types.Feed | null; 
  setFeedContext: (feed: Types.Feed) => void 
} 

// FeedContext 생성
const FeedContext = createContext< FeedContextType | undefined >(undefined);

// FeedProvider 컴포넌트로 상태 관리 제공
export const FeedProvider = ({ children }: { children: React.ReactNode }) => {
  const [feedContext, setFeedContext] = useState<Types.Feed| null>(null);
  const [isLoaded, setIsLoaded] = useState(false); // 로컬스토리지에서 복구 완료 여부

  // 로컬스토리지에서 데이터 복구
  useEffect(() => {
    const savedFeed = localStorage.getItem("feedContext");
    if (savedFeed) {
      setFeedContext(JSON.parse(savedFeed));
    }
    setIsLoaded(true); // 복구 완료
  }, []);

  // feedContext가 변경될 때마다 로컬스토리지 저장
  useEffect(() => {
    if (feedContext) {
      localStorage.setItem("feedContext", JSON.stringify(feedContext));
    }
  }, [feedContext]);

  return (
    <FeedContext.Provider value={{ feedContext, setFeedContext }}>
      {children}
    </FeedContext.Provider>
  );
};

// FeedContext를 쉽게 사용할 수 있도록 커스텀 훅
export const useFeed = () => {
  const context = useContext(FeedContext);
  if (!context) {
    throw new Error('useFeeds must be used within a FeedProvider');
  }
  return context;
};
