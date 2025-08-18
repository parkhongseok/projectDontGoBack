"use client";

import { Stack } from "react-bootstrap";

import "../../globals.css";
import styles from "./Feed.module.css";
import { usePathname } from "next/navigation";
import { useFeed } from "../../contexts/FeedContext";
import { httpRequest } from "../../utils/httpRequest";
import * as Types from "../../utils/types";
import Dummys from "../../utils/dummyData";
import { BACKEND_API_URL } from "../../utils/globalValues";

type propsType = {
  feedId: number;
  setIsFeedDeleteOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function DeletePopUp({ feedId, setIsFeedDeleteOpen }: propsType) {
  const { setCrudMyFeed, setFeedContext } = useFeed();
  const pathname = usePathname();
  // 현재 페이지 확인
  const isNowPostDetailPage = /\/post\/\d+$/.test(pathname);

  const handleClosePopUp = () => {
    setIsFeedDeleteOpen(false);
  };

  const handleSubmit = async () => {
    const method = "DELETE";
    const url = `${BACKEND_API_URL}/v1/feeds/${feedId}`;
    const body = null;
    const success = (result: Types.ResData<{ feedId: number }>) => {
      handleClosePopUp();
      if (pathname === "/" || /\/profile\/\d+$/.test(pathname)) {
        setFeedContext({ ...Dummys.Feed, feedId: result.data.feedId });
        setCrudMyFeed({ C: false, R: false, U: false, D: true });
      }
      // 삭제 후 페이지 이동
      if (!isNowPostDetailPage) return;
      window.history.back();
    };
    const fail = () => {
      alert("게시물을 삭제할 수 없습니다.");
    };
    httpRequest(method, url, body, success, fail);
  };

  return (
    <div className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}>
      <div className="sidebar-space" />
      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3"></p>
        {/* 본격 사용 가능 공간 */}
        <Stack gap={4} className={`${styles.deleteBoxContainer}  pt-4`}>
          <div className={` pt-2 fontBlack`}>
            <h5>정말 삭제하시겠습니까</h5>
          </div>
          <div className={`fontGray4 pb-2`}>삭제한 게시물은 복원할 수 없습니다.</div>
          {/* 상단  취소 / 게시글 작성 / ... */}
          <Stack className={`${styles.deleteBtns} `} direction="horizontal">
            <div className={`${styles.deleteBtn} ms-auto `}>
              <button className={` fontGray4`} onClick={handleClosePopUp}>
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
  );
}
