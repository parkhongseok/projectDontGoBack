'use client'

import { Stack } from "react-bootstrap";
import "../globals.css";
import styles from "./Feed.module.css"
import { redirect, useRouter } from "next/navigation";

type propsType = { 
  FeedId : number;
  setShowDeleteBox: React.Dispatch<React.SetStateAction<boolean>>;
  }

export default function DeleteBox( { FeedId, setShowDeleteBox } : propsType){
  const router = useRouter();
  const closeBox = () => {
    setShowDeleteBox(false)
  };


  const handleSubmit = async () => {
    try {
      const response = await fetch(`http://localhost:8090/api/v1/feeds/${FeedId}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.ok){
        closeBox();
        router.push("/");
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
        {/* 본격 사용 가능 공간 */}
        <Stack gap={4} className={`${styles.deleteBoxContainer}  pt-4`}>
            <div className={` pt-2 fontGray1`}>정말 삭제하시겠습니까</div>
            <div className={`fontGray2 pb-2`}>삭제한 게시물은 복원할 수 없습니다.</div>
            {/* 상단  취소 / 게시글 작성 / ... */}
            <Stack className={`${styles.deleteBtns} `} direction="horizontal">
              <div className={`${styles.deleteBtn} ms-auto `} >
                <button className={` fontWhite`} onClick={closeBox}>
                  취소
                </button>
              </div>
              <div className={`${styles.deleteBtn} ${styles.deleteBtnLine} ms-auto `}>
                <button className={` fontRed `} onClick={handleSubmit}>
                  삭제
                </button>
              </div>
            </Stack>
        </Stack>
      </div>
    </div>
  )
}


