"use client";

import { Dispatch, SetStateAction, useEffect, useState } from "react";

import "../../globals.css";

import { Stack } from "react-bootstrap";
import * as Types from "../../utils/types";
import styles from "../Feed.module.css";
import { useFeed } from "../../contexts/FeedContext";
import { useUser } from "../../contexts/UserContext";
import { httpRequest } from "../../utils/httpRequest";
import { BACKEND_API_URL, MAX_TEXT_LENGTH } from "../../utils/globalValues";

type propsType = { setShowWriteBox: Dispatch<SetStateAction<boolean>>; feed: Types.Feed };

export default function CreateCommentPopUp({ setShowWriteBox, feed }: propsType) {
  const [userInput, setUserInput] = useState("");
  const { setCrudMyComment, setCommentContext } = useFeed();
  const { userContext } = useUser();

  const closeBox = () => {
    setShowWriteBox(false);
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
    const CreateCommentRequest = {
      feedId: feed.feedId,
      content: userInput,
      parentCommentId: null,
    };
    console.log(CreateCommentRequest);
    const method = "POST";
    const url = `${BACKEND_API_URL}/v1/comments`;
    const body = CreateCommentRequest;
    const success = (result: Types.ResData<Types.Comment>) => {
      setUserInput("");
      closeBox();
      // 경로 검사 필요하지 않나?
      setCommentContext(result.data);
      setCrudMyComment({ C: true, R: false, U: false, D: false });
    };
    const fail = () => {
      alert("서버 오류가 발생했습니다.");
    };
    httpRequest(method, url, body, success, fail);
  };

  // 모달 body 스크롤 토글
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  // 작성하는 글의 높이 맞춤 설정
  const autoResize = (e: React.FormEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    target.style.height = "auto"; // 먼저 높이를 auto로 리셋
    target.style.height = `${target.scrollHeight}px`; // 텍스트의 높이에 맞게 설정
  };

  if (!userContext) {
    return <div className="loading" />;
  }
  const feedTypeClass = styles[userContext.userType] || "";
  const PLACEHOLER = feed?.userId == userContext?.userId ? `나에게` : `${feed.author} 님에게`;
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
                <button
                  className={`${styles.write} ${styles.exitBtn} custom-button`}
                  onClick={closeBox}
                >
                  취소
                </button>
              </>
              <h6 className={`ms-auto ${styles.createBoxTop}`}>답글 작성하기</h6>
              <h6 className={`ms-auto more ${styles.createBoxTop} pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontGray4  mt-4" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <p className={`${feedTypeClass} ${styles.userName} fontRedLight init mt-2`}>
                {userContext.userName}
              </p>
              <textarea
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontGray4`}
                placeholder={`${PLACEHOLER} 답글 남기기 ...`}
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
