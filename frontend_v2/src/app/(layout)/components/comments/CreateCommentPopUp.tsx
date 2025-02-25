"use client";

import { Dispatch, SetStateAction, useEffect, useState } from "react";

import "../../globals.css";
import { usePathname } from "next/navigation";
import { Button, Stack } from "react-bootstrap";
import * as Types from "../../utils/types";
import styles from "../Feed.module.css";
import { useFeed } from "../../contexts/FeedContext";
import { useUser } from "../../contexts/UserContext";
import { httpRequest } from "../../utils/httpRequest";

type propsType = { setShowWriteBox: Dispatch<SetStateAction<boolean>>; feed: Types.Feed };

export default function CreateCommentPopUp({ setShowWriteBox, feed }: propsType) {
  // const pathname = usePathname() || "";
  const [userInput, setUserInput] = useState("");
  const { setCrudMyComment, setCommentContext } = useFeed();
  const { userContext } = useUser();
  if (!userContext) {
    return <div className="loading" />;
  }
  const feedTypeClass = styles[userContext.userType] || "";
  const closeBox = () => {
    setShowWriteBox(false);
  };
  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setUserInput(e.target.value);
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
    const url = "http://localhost:8090/api/v1/comments";
    const body = CreateCommentRequest;
    const success = (result: any) => {
      setUserInput("");
      closeBox();
      console.log(result.data);
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
                <Button className={`${styles.write} ${styles.exitBtn}`} onClick={closeBox}>
                  취소
                </Button>
              </>
              <h6 className={`ms-auto fontWhite`}>답글 작성하기</h6>
              <h6 className={`ms-auto fontWhite pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontWhite" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <p className={`${feedTypeClass} ${styles.userName} fontRedLight init mt-2`}>
                {userContext.userName}
              </p>
              <textarea
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontWhite`}
                placeholder={`${feed.author} 님에게 답글 남기기 ...`}
                value={userInput}
                onChange={handleChange}
                // disabled
                // readOnly
              />
              <>
                <Button
                  className={`ms-auto ${styles.write} ${styles.createBtn} `}
                  onClick={handleSubmit}
                >
                  게시
                </Button>
              </>
            </Stack>
          </Stack>
        </div>
      </div>
    </div>
  );
}
