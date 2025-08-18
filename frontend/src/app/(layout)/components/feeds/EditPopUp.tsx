"use client";

import "../../globals.css";
import styles from "./Feed.module.css";
import * as Types from "../../utils/types";
import { Stack } from "react-bootstrap";
import { useFeed } from "../../contexts/FeedContext";
import { useEffect, useRef, useState } from "react";
import { usePathname } from "next/navigation";
import { httpRequest } from "../../utils/httpRequest";
import { BACKEND_API_URL, MAX_TEXT_LENGTH } from "../../utils/globalValues";
import Badge from "../badge/Badge";
import BadgeMe from "../badge/BadgeMe";

type propsType = {
  setIsFeedEditOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function EditPopUp({ setIsFeedEditOpen }: propsType) {
  const textareaRef = useRef<HTMLTextAreaElement | null>(null); // 초기 높이 설정
  const pathname = usePathname() || "";
  const { feedContext, setFeedContext, setCrudMyFeed } = useFeed();
  const [userInput, setUserInput] = useState(feedContext?.content || "");

  const autoResize = (input: HTMLTextAreaElement | React.FormEvent<HTMLTextAreaElement>) => {
    const target =
      input instanceof HTMLTextAreaElement ? input : (input.target as HTMLTextAreaElement);
    target.style.height = "auto";
    target.style.height = `${target.scrollHeight}px`;
  };

  useEffect(() => {
    if (textareaRef.current) {
      autoResize(textareaRef.current);
    }
  }, []);

  if (!feedContext) return <div className="loading" />;
  const setContentContext = (newContent: string, newUpdatedAt: string) => {
    const newFeed = {
      ...feedContext,
      content: newContent,
      updatedAt: newUpdatedAt,
    };
    setFeedContext(newFeed);
  };

  const handleClosePopUp = () => {
    setIsFeedEditOpen(false);
  };

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const textarea = e.target;
    let text = textarea.value;

    const cursorPosition = textarea.selectionStart; // 현재 커서 위치 저장

    text = text.replace(/\n{3,}/g, "\n\n");
    if (text.length <= MAX_TEXT_LENGTH) {
      setUserInput(text);
    }
    // 상태 업데이트 이후 커서 위치 복구
    setTimeout(() => {
      textarea.selectionStart = textarea.selectionEnd = cursorPosition;
    }, 0);
  };

  const handleSubmit = async () => {
    if (!userInput.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 요청 객체
    const updateFeedRequest = {
      content: userInput,
    };

    const method = "PATCH";
    const url = `${BACKEND_API_URL}/v1/feeds/${feedContext.feedId}`;
    const body = updateFeedRequest;
    const success = (
      result: Types.ResData<{ feedId: number; content: string; updatedAt: string }>
    ) => {
      if (result.data.feedId != feedContext.feedId)
        return console.error("EditPopUp : 즉시 갱신 실패");
      setContentContext(result.data.content, result.data.updatedAt);
      if (pathname === "/" || /\/profile\/\d+$/.test(pathname)) {
        setCrudMyFeed({ C: false, R: false, U: true, D: false });
      }
      setIsFeedEditOpen(false);
    };
    const fail = () => {
      alert("게시물을 수정할 수 없습니다.");
    };
    httpRequest(method, url, body, success, fail);
  };

  // 뱃지를 렌더링하는 헬퍼 함수
  const renderBadge = () => {
    return (
      <>
        {/* 역할(Role) 기반 뱃지 */}
        {feedContext.userRole === "ADMIN" && <Badge role="admin">관리자</Badge>}
        {feedContext.userRole === "GUEST" && <Badge role="guest">방문자</Badge>}

        {/* '나' 뱃지 (역할과 별개로 항상 표시) */}
        {feedContext.userId === feedContext.userId && <BadgeMe role="me">나</BadgeMe>}
      </>
    );
  };

  const feedTypeClass = styles[feedContext.feedType] || "";
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
            <div className="d-flex justify-content-between align-items-center mx-5">
              {/* 왼쪽: 취소 버튼 */}
              <button
                className={`${styles.write} ${styles.exitBtn} custom-button`}
                onClick={handleClosePopUp}
              >
                취소
              </button>

              {/* 중앙: 제목 */}
              <h6 className={`${styles.createBoxTop} m-0`}>답글 수정</h6>

              {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
              <button
                className={`${styles.write} ${styles.exitBtn} custom-button`}
                style={{ visibility: "hidden" }}
                aria-hidden="true"
              >
                취소
              </button>
            </div>
            <hr className="feed-underline fontGray4 mt-4" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <div className="d-flex align-items-center mt-2">
                {/* 이름과 뱃지를 정렬하기 위한 div */}
                <p className={`${feedTypeClass} ${styles.userName} fontRedLight init`}>
                  {feedContext.author}
                </p>
                {renderBadge()} {/* 헬퍼 함수 호출 */}
              </div>
              <textarea
                ref={textareaRef}
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontGray4`}
                placeholder="게시글 수정하기"
                value={userInput}
                onChange={handleChange}
                maxLength={MAX_TEXT_LENGTH}
              />
              <p className={`ms-auto mb-1 ${styles.maxLength}`}>
                {userInput.length} / {MAX_TEXT_LENGTH}
              </p>
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
