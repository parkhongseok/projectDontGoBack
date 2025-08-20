import React, { useEffect } from "react";
import styles from "../feeds/Feed.module.css";
import "../../globals.css";
import { Stack } from "react-bootstrap";

type PolicyPopupProps = {
  title: string;
  onPolicyClose: () => void;
  children: React.ReactNode;
};

const PolicyPopup: React.FC<PolicyPopupProps> = ({ title, onPolicyClose, children }) => {
  // 배경 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  return (
    <>
      <div className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}>
        <div className="sidebar-space" />
        <div className="main-space">
          {/* dropdown 버튼이 들어올 자리 */}
          <p className="text-center mb-4 pt-3"></p>
          <div className={`pt-4 ${styles.createBoxContainer}`}>
            {/* 사이드바가 차지하지 않는 나머지 공간 */}

            {/* 본격 사용 가능 공간 */}
            <Stack gap={1} direction="vertical" className="pb-4">
              {/* 상단  취소 / 게시글 작성 / ... */}
              <div className="d-flex justify-content-between align-items-center mx-5">
                {/* 왼쪽: 취소 버튼 */}

                <button
                  onClick={onPolicyClose}
                  className={`${styles.write} ${styles.exitBtn} custom-button`}
                >
                  확인
                </button>

                {/* 중앙: 제목 */}
                <h6 className={`${styles.createBoxTop} m-0`}>{title}</h6>

                {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
                <button
                  className={`${styles.write} ${styles.exitBtn} custom-button`}
                  style={{ visibility: "hidden" }}
                  aria-hidden="true"
                >
                  확인
                </button>
              </div>
              <hr className="feed-underline fontGray4 mt-3" />

              {/* 글쓰기 영역*/}
              <Stack gap={3} className="mx-3">
                <div className={`mx-auto ${styles.content}`}>{children}</div>
                <>
                  <button
                    onClick={onPolicyClose}
                    className={`ms-auto mb-1 ${styles.write} custom-button`}
                  >
                    확인
                  </button>
                </>
              </Stack>
            </Stack>
          </div>
        </div>
      </div>
    </>
  );
};

export default PolicyPopup;
