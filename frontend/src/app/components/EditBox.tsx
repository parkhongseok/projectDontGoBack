'use client'

import "../globals.css";
import styles from "./Feed.module.css"
import {Stack} from 'react-bootstrap';
import Dummys from "../dummyData";
import { useFeed } from "../context/FeedContext";
import { useEffect, useState } from "react";

type propsType = { 
  setShowEditBox: React.Dispatch<React.SetStateAction<boolean>>
  }

export default function EditBox({ setShowEditBox } : propsType){
  const user = Dummys.User;
  const { feedContext, setFeedContext } = useFeed();

  if (!feedContext) {
    return <div className="loading"/>;  // feed가 없으면 로딩 중인 상태 표시 공간 컴포넌트로 대체 고민
  }
  const [feed, setFeed] = useState(feedContext);
  const feedTypeClass = styles[feedContext.feedType] || "";

  const setContent = (newContent : string) =>{
    setFeed({
      ...feed,
      content: newContent,
    })
  };
  const setContentCotext = (newContent : string) =>{
    setFeedContext({
      ...feed,
      content: newContent,
    })
  };

  const closeBox = () => {
    setShowEditBox(false)
  };

  const autoResize = (e : React.FormEvent<HTMLTextAreaElement>) => {
    const target = e.target as HTMLTextAreaElement;
    target.style.height = 'auto';  // 먼저 높이를 auto로 리셋
    target.style.height = `${target.scrollHeight}px`;  // 텍스트의 높이에 맞게 설정
  };

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setContent(e.target.value); 
  };

  const handleSubmit = async () => {
    if (!feedContext.content.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 요청 객체
    const postData = {
      content : feed.content,
    };

    try {
      const response = await fetch(`http://localhost:8090/api/v1/feeds/${feed.feedId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(postData),
      });

      if (response.ok){
        setContentCotext(postData.content);
        setShowEditBox(false);
        // 응답 객체를 context에 반영해야함
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
                {feedContext.userName}
              </p>
              <textarea 
                onInput={(e) => autoResize(e)}
                rows={5} 
                className={`${styles.textBox} fontWhite`}
                placeholder="게시글 작성하기"
                value = {feed.content} 
                onChange={handleChange}
                // disabled
                // readOnly
              />
              <>
              <button 
              className={`ms-auto ${styles.write}`}
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
  )
}


