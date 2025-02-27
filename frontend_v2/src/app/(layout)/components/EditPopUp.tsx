"use client";

import "../globals.css";
import styles from "./Feed.module.css";
import { Button, Stack } from "react-bootstrap";
import { useFeed } from "../contexts/FeedContext";
import { useState } from "react";
import { usePathname } from "next/navigation";
import { httpRequest } from "../utils/httpRequest";

type propsType = {
  setIsFeedEditOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function EditPopUp({ setIsFeedEditOpen }: propsType) {
  const pathname = usePathname() || "";
  const { feedContext, setFeedContext, setCrudMyFeed } = useFeed();

  // feed가 없으면 로딩 중인 상태 표시 공간 컴포넌트로 대체 고민
  if (!feedContext) return <div className="loading" />;

  const [feed, setFeed] = useState(feedContext);

  const feedTypeClass = styles[feedContext.feedType] || "";

  const setUserInput = (newContent: string) => {
    // 구조분해 할당
    setFeed({
      ...feed,
      content: newContent,
    });
  };

  const setContentContext = (newContent: string, newUpdatedAt: string) => {
    const newFeed = {
      ...feed,
      content: newContent,
      updatedAt: newUpdatedAt,
    };
    setFeedContext(newFeed);
  };

  const handleClosePopUp = () => {
    setIsFeedEditOpen(false);
  };

  const autoResize = (e: React.FormEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    target.style.height = "auto"; // 먼저 높이를 auto로 리셋
    target.style.height = `${target.scrollHeight}px`; // 텍스트의 높이에 맞게 설정
  };

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setUserInput(e.target.value);
  };

  const handleSubmit = async () => {
    if (!feed.content.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 요청 객체
    const updateFeedRequest = {
      content: feed.content,
    };

    const method = "PATCH";
    const url = `http://localhost:8090/api/v1/feeds/${feed.feedId}`;
    const body = updateFeedRequest;
    const success = (result: any) => {
      // console.log("EditPopUp : ", feedContext);
      // console.log("result.data : ", result.data);
      if (result.data.feedId == feedContext.feedId)
        setContentContext(result.data.content, result.data.updatedAt);
      else console.error("EditPopUp : 즉시 갱신 실패");
      setIsFeedEditOpen(false);
      if (pathname === "/") {
        setCrudMyFeed({ C: false, R: false, U: true, D: false });
      }
    };
    const fail = () => {
      alert("게시물을 수정할 수 없습니다.");
    };
    httpRequest(method, url, body, success, fail);
  };

  return (
    <div className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}>
      <div className="sidebar-space" />
      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3"></p>
        <div className={`pt-4 ${styles.createBoxContainer}`}>
          {/* 사이드바가 차지하지 않는 나머지 공간 */}

          {/* 본격 사용 가능 공간 */}
          <Stack gap={1} direction="vertical" className="pb-4 pt-2">
            {/* 상단  취소 / 게시글 작성 / ... */}
            <Stack direction="horizontal" className="mx-5">
              <>
                <button className={`${styles.write} custom-button`} onClick={handleClosePopUp}>
                  취소
                </button>
              </>
              <h6 className={`ms-auto ${styles.createBoxTop}`}>게시글 수정하기</h6>
              <h6 className={`ms-auto ${styles.createBoxTop} pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontGray4 mt-4" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <p className={`${feedTypeClass} ${styles.userName} fontRedLight init mt-2`}>
                {feedContext.author}
              </p>
              <textarea
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontWhite`}
                placeholder="게시글 수정하기"
                value={feed.content}
                onChange={handleChange}
                // disabled
                // readOnly
              />
              <>
                <button
                  className={`ms-auto mb-1 ${styles.write} custom-button`}
                  onClick={handleSubmit}
                >
                  게시
                </button>
              </>
            </Stack>
          </Stack>
        </div>
      </div>
    </div>
  );
}
