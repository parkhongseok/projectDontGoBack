"use client";

import "../../globals.css";
import { Stack } from "react-bootstrap";
import styles from "../feeds/Feed.module.css";
import { useFeed } from "../../contexts/FeedContext";
import { httpRequest } from "../../utils/httpRequest";
import * as Types from "../../utils/types";
import Dummys from "../../utils/dummyData";
import { BACKEND_API_URL } from "../../utils/globalValues";
import { useEffect } from "react";

type propsType = {
  commentId: number;
  setIsCommentDeleteOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function DeleteCommentPopUp({ commentId, setIsCommentDeleteOpen }: propsType) {
  // const pathname = usePathname() || "";
  const { setCrudMyComment, setCommentContext } = useFeed();
  // const router = useRouter();
  const handleClosePopUp = () => {
    setIsCommentDeleteOpen(false);
  };

  // 배경 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  const handleSubmit = async () => {
    const method = "DELETE";
    const url = `${BACKEND_API_URL}/v1/comments/${commentId}`;
    const body = null;
    const success = (result: Types.ResData<Types.Comment>) => {
      handleClosePopUp();
      // 경로 검사 안필요할까
      setCommentContext({ ...Dummys.Comment, commentId: result.data.commentId });
      setCrudMyComment({ C: false, R: false, U: false, D: true });
    };
    const fail = () => {
      alert("답글을 삭제할 수 없습니다.");
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
          <div className={`fontGray4 pb-2`}>삭제한 답글은 복원할 수 없습니다.</div>
          {/* 상단  취소 / 게시글 작성 / ... */}
          <Stack className={`${styles.deleteBtns} `} direction="horizontal">
            <div className={`${styles.deleteBtn} ms-auto `}>
              <button className={` fontGray4 `} onClick={handleClosePopUp}>
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
