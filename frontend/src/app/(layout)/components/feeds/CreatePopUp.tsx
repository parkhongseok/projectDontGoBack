"use client";

import "../../globals.css";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import styles from "./Feed.module.css";
import { Stack } from "react-bootstrap";
import { useFeed } from "../../contexts/FeedContext";
import { useUser } from "../../contexts/UserContext";
import { usePathname } from "next/navigation";
import { httpRequest } from "../../utils/httpRequest";
import { BACKEND_API_URL, MAX_TEXT_LENGTH } from "../../utils/globalValues";
import * as Types from "../../utils/types";
import Badge from "../badge/Badge";
import BadgeMe from "../badge/BadgeMe";

type propsType = {
  setIsFeedCreaterOpen: Dispatch<SetStateAction<boolean>>;
};

export default function CreatePopUp({ setIsFeedCreaterOpen }: propsType) {
  // const router = useRouter();
  const pathname = usePathname() || "";
  const { setCrudMyFeed, setFeedContext } = useFeed();
  const [userInput, setUserInput] = useState("");
  const { userContext } = useUser();
  // 모달 body 스크롤 토글
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);
  if (!userContext) {
    return <div className="loading" />;
  }

  const handlerClose = () => {
    setIsFeedCreaterOpen(false);
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
    if (userInput.length == 20) alert("내용을 입력해주세요.");
    if (!userInput.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 요청 객체
    const createFeedRequest = {
      content: userInput,
    };
    const method = "POST";
    const url = `${BACKEND_API_URL}/v1/feeds`;
    const body = createFeedRequest;
    const success = (result: { data: Types.Feed }) => {
      setUserInput("");
      handlerClose();
      // console.log(pathname)
      // main 화면인 경우에만 리프레쉬(추후에 프로필이라면, 프로필 부분을 새로고침하도록 구현)
      if (pathname === "/" || /\/profile\/\d+$/.test(pathname)) {
        setFeedContext(result.data);
        setCrudMyFeed({ C: true, R: false, U: false, D: false });
      }
    };
    const fail = () => {
      alert("서버 오류가 발생했습니다.");
    };

    httpRequest(method, url, body, success, fail);
  };

  // 작성하는 글의 높이 맞춤 설정
  const autoResize = (e: React.FormEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    target.style.height = "auto"; // 먼저 높이를 auto로 리셋
    target.style.height = `${target.scrollHeight}px`; // 텍스트의 높이에 맞게 설정
  };

  // 뱃지를 렌더링하는 헬퍼 함수
  const renderBadge = () => {
    return (
      <>
        {/* 역할(Role) 기반 뱃지 */}
        {userContext.userRole === "ADMIN" && <Badge role="admin">관리자</Badge>}
        {userContext.userRole === "GUEST" && <Badge role="guest">방문자</Badge>}

        {/* '나' 뱃지 (역할과 별개로 항상 표시) */}
        {userContext.userId === userContext.userId && <BadgeMe role="me">나</BadgeMe>}
      </>
    );
  };

  const feedTypeClass = styles[userContext.userType] || "";
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
                  onClick={handlerClose}
                >
                  취소
                </button>
              </>
              <h6 className={`ms-auto ${styles.createBoxTop}`}>글 작성하기</h6>
              <h6 className={`ms-auto more ${styles.createBoxTop} pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontGray4 mt-4" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <div className="d-flex align-items-center  mt-2">
                {/* 이름과 뱃지를 정렬하기 위한 div */}
                <p className={`${feedTypeClass} ${styles.userName} fontRedLight init`}>
                  {userContext.userName}
                </p>
                {renderBadge()} {/* 헬퍼 함수 호출 */}
              </div>
              <textarea
                maxLength={MAX_TEXT_LENGTH}
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontGray4`}
                placeholder="게시글 작성하기"
                value={userInput}
                onChange={handleChange}
                // disabled
                // readOnly
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
