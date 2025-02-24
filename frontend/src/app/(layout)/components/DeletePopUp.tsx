"use client";

import { Stack } from "react-bootstrap";
import "../globals.css";
import styles from "./Feed.module.css";
import { usePathname, useRouter } from "next/navigation";
import { useFeed } from "../contexts/FeedContext";
import { httpRequest } from "../utils/httpRequest";
import * as Types from "../utils/types";

type propsType = {
  FeedId: number;
  setShowDeleteBox: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function DeletePopUp({ FeedId, setShowDeleteBox }: propsType) {
  const pathname = usePathname() || "";
  const { setCrudMyFeed, setFeedContext, feedContext } = useFeed();
  const router = useRouter();
  const closeBox = () => {
    setShowDeleteBox(false);
  };

  const handleSubmit = async () => {
    const method = "DELETE";
    const url = `http://localhost:8090/api/v1/feeds/${FeedId}`;
    const body = null;
    const success = (result: any) => {
      closeBox();
      setCrudMyFeed({ C: false, R: false, U: false, D: true });
      setFeedContext(result.data);
      if (pathname !== "/") {
        router.push("/");
      }
    };
    const fail = () => {
      alert("게시물을 삭제할 수 없습니다.");
    };
    httpRequest(method, url, body, success, fail);
  };

  return (
    <div
      className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}
    >
      <div className="sidebar-space" />
      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3"></p>
        {/* 본격 사용 가능 공간 */}
        <Stack gap={4} className={`${styles.deleteBoxContainer}  pt-4`}>
          <div className={` pt-2 fontGray1`}>정말 삭제하시겠습니까</div>
          <div className={`fontGray2 pb-2`}>
            삭제한 게시물은 복원할 수 없습니다.
          </div>
          {/* 상단  취소 / 게시글 작성 / ... */}
          <Stack className={`${styles.deleteBtns} `} direction="horizontal">
            <div className={`${styles.deleteBtn} ms-auto `}>
              <button className={` fontWhite`} onClick={closeBox}>
                취소
              </button>
            </div>
            <div
              className={`${styles.deleteBtn} ${styles.deleteBtnLine} ms-auto `}
            >
              <button className={` fontRed `} onClick={handleSubmit}>
                삭제
              </button>
            </div>
          </Stack>
        </Stack>
      </div>
    </div>
  );
}
