"use client";

import "../../globals.css";
import styles from "../Feed.module.css";
import { Stack } from "react-bootstrap";
import { useFeed } from "../../contexts/FeedContext";
import { useEffect, useRef, useState } from "react";
import { httpRequest } from "../../utils/httpRequest";
import { MAX_TEXT_LENGTH } from "../../utils/values";

type propsType = {
  setIsCommentEditOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function EditCommentPopUp({ setIsCommentEditOpen }: propsType) {
  const { commentContext, setCommentContext, setCrudMyComment } = useFeed();
  if (!commentContext) return <div className="loading" />;
  const [userInput, setUserInput] = useState(commentContext?.content || "");

  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  useEffect(() => {
    if (textareaRef.current) {
      autoResize({ target: textareaRef.current } as any); // 초기 높이 설정
    }
  }, []);

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

  const autoResize = (e: React.FormEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    target.style.height = "auto";
    target.style.height = `${target.scrollHeight}px`;
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
    const url = `http://localhost:8090/api/v1/comments/${commentContext.commentId}`;
    const body = updateRequest;
    const success = (result: any) => {

      if (result.data.commentId == commentContext.commentId)
        setContentContext(result.data.content, result.data.updatedAt);
      else console.error("EditPopUp : 즉시 갱신 실패");
      // 경로 검사 ㄱㅊ?
      setIsCommentEditOpen(false);
      setCrudMyComment({ C: false, R: false, U: true, D: false });
    };
    const fail = () => {
      alert("게시물을 수정할 수 없습니다.");
    };
    httpRequest(method, url, body, success, fail);
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
            <Stack direction="horizontal" className="mx-5">
              <>
                <button className={`${styles.write} custom-button`} onClick={handleClosePopUp}>
                  취소
                </button>
              </>
              <h6 className={`ms-auto ${styles.createBoxTop}`}>답글 수정하기</h6>
              <h6 className={`ms-auto ${styles.createBoxTop} pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontGray4 mt-4" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <p className={`${commentTypeClass} ${styles.userName} fontRedLight init mt-2`}>
                {commentContext.author}
              </p>
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
