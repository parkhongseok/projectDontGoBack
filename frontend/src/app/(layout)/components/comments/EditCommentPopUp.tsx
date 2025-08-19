"use client";

import "../../globals.css";
import styles from "../feeds/Feed.module.css";
import * as Types from "../../utils/types";
import { Stack } from "react-bootstrap";
import { useFeed } from "../../contexts/FeedContext";
import { useEffect, useRef, useState } from "react";
import { httpRequest } from "../../utils/httpRequest";
import { BACKEND_API_URL, MAX_TEXT_LENGTH } from "../../utils/globalValues";
import Badge from "../badge/Badge";
import BadgeMe from "../badge/BadgeMe";

type propsType = {
  setIsCommentEditOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function EditCommentPopUp({ setIsCommentEditOpen }: propsType) {
  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  const { commentContext, setCommentContext, setCrudMyComment } = useFeed();
  const [userInput, setUserInput] = useState(commentContext?.content || "");

  const autoResize = (input: HTMLTextAreaElement | React.FormEvent<HTMLTextAreaElement>) => {
    const target =
      input instanceof HTMLTextAreaElement ? input : (input.target as HTMLTextAreaElement);
    target.style.height = "auto";
    target.style.height = `${target.scrollHeight}px`;
  };

  // 배경 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  useEffect(() => {
    if (textareaRef.current) {
      autoResize(textareaRef.current);
    }
  }, []);

  if (!commentContext) return <div className="loading" />;
  const setContentContext = (newContent: string, newUpdatedAt: string) => {
    const newComment = {
      ...commentContext,
      content: newContent,
      updatedAt: newUpdatedAt,
    };
    setCommentContext(newComment);
  };

  const handleClosePopUp = () => {
    setIsCommentEditOpen(false);
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
    const updateRequest = {
      content: userInput,
    };

    const method = "PATCH";
    const url = `${BACKEND_API_URL}/v1/comments/${commentContext.commentId}`;
    const body = updateRequest;
    const success = (result: Types.ResData<Types.Comment>) => {
      if (result.data.commentId == commentContext.commentId)
        setContentContext(result.data.content, result.data.updatedAt);
      else console.error("EditPopUp : 즉시 갱신 실패");
      setIsCommentEditOpen(false);
      setCrudMyComment({ C: false, R: false, U: true, D: false });
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
        {commentContext.userRole === "ADMIN" && <Badge role="admin">관리자</Badge>}
        {commentContext.userRole === "GUEST" && <Badge role="guest">방문자</Badge>}

        {/* '나' 뱃지 (역할과 별개로 항상 표시) */}
        {commentContext.userId === commentContext.userId && <BadgeMe role="me">나</BadgeMe>}
      </>
    );
  };

  const commentTypeClass = styles[commentContext.commentType] || "";
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
              <h6 className={`${styles.createBoxTop} m-0`}>게시글 수정</h6>

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
                <p className={`${commentTypeClass} ${styles.userName} fontRedLight init mt-2`}>
                  {commentContext.author}
                </p>
                {renderBadge()} {/* 헬퍼 함수 호출 */}
              </div>
              <textarea
                ref={textareaRef}
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontGray4`}
                placeholder="답글 수정하기"
                value={userInput}
                onChange={handleChange}
                maxLength={MAX_TEXT_LENGTH}
              />
              <p className={`ms-auto mb-1 ${styles.maxLength}`}>
                {userInput.length} / {MAX_TEXT_LENGTH}
              </p>
              <>
                <button className={`ms-auto ${styles.write} custom-button`} onClick={handleSubmit}>
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
