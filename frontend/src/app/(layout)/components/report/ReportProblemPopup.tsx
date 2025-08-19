import React, { useEffect, useState } from "react";
import { useUser } from "../../contexts/UserContext";
import styles from "../feeds/Feed.module.css";
import { Stack } from "react-bootstrap";
import {  MAX_TEXT_LENGTH } from "../../utils/globalValues";

type ReportProblemPopupProps = {
  onClose: () => void;
};

const ReportProblemPopup: React.FC<ReportProblemPopupProps> = ({ onClose }) => {
  // 배경 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  const handleSubmit = () => {
    // API가 준비되면 여기에 연동합니다.
    if (userInput.length == 20) alert("내용을 입력해주세요.");
    if (!userInput.trim()) {
      alert("내용을 입력해주세요.");
      return;
    }
    // 예: sendReport(reportContent);
    alert("문제 신고가 접수되었습니다. (현재는 프론트엔드 기능만 구현되어 있습니다.)");
    onClose(); // 팝업 닫기
  };

  const [userInput, setUserInput] = useState("");
  const { userContext } = useUser();

  // 배경 스크롤 잠금
  useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  if (!userContext) {
    return <div className="loading" />;
  }

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

  // const handleSubmit = async () => {
  //   if (userInput.length == 20) alert("내용을 입력해주세요.");
  //   if (!userInput.trim()) {
  //     alert("내용을 입력해주세요.");
  //     return;
  //   }
  //   // 요청 객체
  //   const createFeedRequest = {
  //     content: userInput,
  //   };
  //   const method = "POST";
  //   const url = `${BACKEND_API_URL}/v1/feeds`;
  //   const body = createFeedRequest;
  //   const success = (result: { data: Types.Feed }) => {
  //     setUserInput("");
  //     handlerClose();
  //     // console.log(pathname)
  //     // main 화면인 경우에만 리프레쉬(추후에 프로필이라면, 프로필 부분을 새로고침하도록 구현)
  //     if (pathname === "/" || /\/profile\/\d+$/.test(pathname)) {
  //       setFeedContext(result.data);
  //       setCrudMyFeed({ C: true, R: false, U: false, D: false });
  //     }
  //   };
  //   const fail = () => {
  //     alert("서버 오류가 발생했습니다.");
  //   };

  //   httpRequest(method, url, body, success, fail);
  // };

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
            <div className="d-flex justify-content-between align-items-center mx-5">
              {/* 왼쪽: 취소 버튼 */}
              <button
                className={`${styles.write} ${styles.exitBtn} custom-button`}
                onClick={onClose}
              >
                취소
              </button>

              {/* 중앙: 제목 */}
              <h6 className={`${styles.createBoxTop} m-0`}>문제 신고</h6>

              {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
              <button
                className={`${styles.write} ${styles.exitBtn} custom-button`}
                style={{ visibility: "hidden" }}
                aria-hidden="true"
              >
                취소
              </button>
            </div>
            <hr className="feed-underline fontGray4 mt-4" />

            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <div className="d-flex align-items-center  mt-2">
                {/* 이름과 뱃지를 정렬하기 위한 div */}
                {/* <p className={`${feedTypeClass} ${styles.userName} fontRedLight init`}>
                  {userContext.userName}
                </p>
                {renderBadge()} 헬퍼 함수 호출 */}
              </div>
              <textarea
                maxLength={MAX_TEXT_LENGTH}
                onInput={(e) => autoResize(e)}
                rows={5}
                className={`${styles.textBox} fontGray4`}
                placeholder="서비스 이용 중 발생한 문제나 개선점을 알려주세요."
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
                  전송
                </button>
              </>
            </Stack>
          </Stack>
        </div>
      </div>
    </div>
    // <div className={styles.overlay}>
    //   <div className={styles.popup}>
    //     <div className={styles.header}>
    //       <h2 className={styles.title}>문제 신고</h2>
    //       <button onClick={onClose} className={styles.closeButton}>
    //         <FontAwesomeIcon icon={faXmark} width={24} height={24} />
    //       </button>
    //     </div>
    //     <div className={styles.content}>
    //       <p>서비스 이용 중 발생한 문제나 개선점을 알려주세요.</p>
    //       <textarea
    //         className={styles.textarea}
    //         value={reportContent}
    //         onChange={(e) => setReportContent(e.target.value)}
    //         placeholder="문제 내용을 자세히 적어주세요."
    //       />
    //     </div>
    //     <div className={styles.actions}>
    //       <button onClick={onClose} className={`${styles.button} ${styles.cancelButton}`}>
    //         취소
    //       </button>
    //       <button onClick={handleSubmit} className={`${styles.button} ${styles.submitButton}`}>
    //         제출
    //       </button>
    //     </div>
    //   </div>
    // </div>
  );
};

export default ReportProblemPopup;
