'use client'

import { useState } from "react";
import "../globals.css";
import styles from "./Feed.module.css"
import {Button, Stack} from 'react-bootstrap';
import { useFeed } from "../contexts/FeedContext";
import { useUser } from "../contexts/UserContext";
import { usePathname } from "next/navigation";

type propsType = {
    setShowWriteBox: React.Dispatch<React.SetStateAction<boolean>>
  }

export default function CreateBox({ setShowWriteBox } : propsType){
  const { userContext } = useUser();
  if (!userContext){
    return <div className="loading"/>;  
  }
  const feedTypeClass = styles[userContext.userType] || "";
  const { refreshMainFeeds, setRefreshMainFeeds } = useFeed();
  const autoResize = (e : React.FormEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    target.style.height = 'auto';  // 먼저 높이를 auto로 리셋
    target.style.height = `${target.scrollHeight}px`;  // 텍스트의 높이에 맞게 설정
  };
    const closeBox = () => {
      setShowWriteBox(false)
  };

  const [content, setContent] = useState("");
  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setContent(e.target.value); 
  };
  const handleSubmit = async () => {
    if (!content.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 요청 객체
    const feedRequest = {
      content : content,
    };

    try {
      const response = await fetch("http://localhost:8090/api/v1/feeds", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(feedRequest),
      });

      if (response.ok){
        setContent("");
        setShowWriteBox(false);
        setRefreshMainFeeds(!refreshMainFeeds)
        const pathname = usePathname();
        // main 화면인 경우에만 리프레쉬(추후에 프로필이라면, 프로필 부분을 새로고침하도록 구현)
        if (pathname === "/"){
          setRefreshMainFeeds(!refreshMainFeeds)
        }
      } else { 
        alert("잠시 후 다시 시도해주세요.");
      }
    } catch (error) {
      console.log("Error : ", error)
      alert("서버 오류가 발생했습니다.")
    }

  }
  return (
  <div className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}>
      <div className="sidebar-space"/>
      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3"></p>
        <div className={`pt-4 ${styles.createBoxContainer}`}>
          {/* 사이드바가 차지하지 않는 나머지 공간 */}

          {/* 본격 사용 가능 공간 */}
          <Stack gap={1} direction="vertical" className="pb-4 pt-2" >
            {/* 상단  취소 / 게시글 작성 / ... */}
            <Stack direction="horizontal" className="mx-5">
              <>
              <button className={`${styles.write}`} onClick={closeBox}>
                취소
              </button>
              </>
              <h6 className={`ms-auto fontWhite`}>
                게시글 쓰기
              </h6>
              <h6 className={`ms-auto fontWhite pb-2`}>
                . . . 
              </h6>
            </Stack>
            <hr className="feed-underline fontWhite"/>

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <p className={`${feedTypeClass} ${styles.userName} fontRedLight init mt-2`}>
                {userContext.userName}
              </p>
              <textarea 
                onInput={(e) => autoResize(e)}
                rows={5} 
                className={`${styles.textBox} fontWhite`}
                placeholder="게시글 작성하기"
                value={content} 
                onChange={handleChange}
                // disabled
                // readOnly
              />
              <>
              <Button 
              className={`ms-auto ${styles.write}`}
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
  )
}


