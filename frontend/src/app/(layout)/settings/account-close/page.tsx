"use client";

import "../../globals.css";
import styles from "../../components/feeds/Feed.module.css";
import { useUser } from "../../contexts/UserContext";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEnvelope } from "@fortawesome/free-solid-svg-icons/faEnvelope";
import { Stack } from "react-bootstrap";
import { BACKEND_API_URL } from "../../utils/globalValues";
// import * as Types from "../../utils/types";
import { httpRequest } from "../../utils/httpRequest";
import { useRouter } from "next/navigation";
import { useState } from "react";
import GoBackButton from "../../components/buttons/GoBackButton";

export default function CloseAccount() {
  const [isEmailSend, setIsEmailSend] = useState(false);
  const { userContext } = useUser();
  const router = useRouter();

  if (!userContext) {
    return <div className="loading" />;
  }

  const handlerBefore = () => {
    router.back();
  };
  const handlerAccountCloseEamil = () => {
    const method = "POST";
    const url = `${BACKEND_API_URL}/v1/users/account-close/email-request`;
    const body = null;
    const success = () =>
      // result: Types.ResData<{ feedId: number; content: string; updatedAt: string }>
      {
        alert("이메일을 확인해 주세요! 탈퇴 링크가 전송되었습니다.");
        localStorage.removeItem("access_token");
        setIsEmailSend(true);
        // router.push("/login");
      };
    const fail = () => {
      alert("탈퇴 이메일 전송에 실패했습니다. 다시 시도해 주세요.");
    };
    if (isEmailSend) alert("이미 이메일이 전송 되었습니다.");
    if (userContext.userRole !== "GUEST") {
      httpRequest(method, url, body, success, fail);
    } else {
      alert("방문자 계정은 삭제하실 수 없습니다.");
    }
  };

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <div className="d-flex justify-content-between align-items-center">
        {/* 왼쪽: 뒤로가기 버튼 */}
        <GoBackButton size={30} />

        {/* 중앙: 제목 (m-0으로 기본 마진 제거) */}
        <h5 className="topTitleText m-0">Settings</h5>

        {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
        <div style={{ width: `${30}px` }} />
      </div>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`feed-detail-container`}>
        {/* 본격 사용 가능 공간 */}
        <>
          <div
            className={`d-flex justify-content-between align-items-center ${styles.sideArea} mt-3`}
          >
            {/* 왼쪽: 취소 버튼 */}
            <button
              className={`${styles.write} ${styles.exitBtn} custom-button`}
              onClick={handlerBefore}
              // style={{ visibility: "hidden" }}
              // aria-hidden="true"
            >
              이전 페이지
            </button>

            {/* 중앙: 제목 */}
            <h4 className={`fontRed m-0 fw-bold my-2 fontRed`}>계정 삭제</h4>
            {/* <h2 className="fw-bold mt-3 mb-3 text-center fontRed">계정 삭제</h2> */}

            {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
            <button
              className={`${styles.write} ${styles.exitBtn} custom-button`}
              style={{ visibility: "hidden" }}
              aria-hidden="true"
            >
              이전 페이지
            </button>
          </div>

          <hr className="feed-underline fontGray4 mt-3" />
          <div className="">
            <div className={`overflow-hidden ${styles.leftMargin}`}>
              {/* 상단: 설명 영역 */}
              <div className="mx-3 mt-4">
                <p className=" text-center">
                  계정을 삭제하면, <br />
                  모든 데이터가
                  <strong className="fontRed"> 영구적</strong>
                  으로 삭제됩니다.
                </p>
                <ul className="mt-3 list-unstyled mx-1">
                  {/* <li>✔ 작성한 게시글 및 댓글은 복구되지 않습니다.</li>
                  <li>
                    ✔ <strong>이메일 인증</strong>을 통해 계속 진행하실 수 있습니다.
                  </li> */}
                  <li>
                    ✔ 메일 인증 후 <strong> 2주</strong> 뒤 탈퇴가 완료됩니다.
                  </li>
                  <li>
                    ✔ <strong> 2주 이내</strong>에 로그인하면, 자동으로 탈퇴가 철회됩니다.
                  </li>
                </ul>
              </div>
            </div>
            <hr className="feed-underline fontGray3 pb-4 mt-4" />
            <div className="mx-5 mb-5">
              {/* 하단: 버튼 영역 */}
              <div className="text-center rounded-4 bgGray4 py-5 ">
                <p className={`mb-3 fontWhite ${styles.leftMargin} `}>
                  계속 진행하길 원하신다면
                  <br /> 아래 버튼을 클릭해주세요.
                </p>

                <p className="mb-3 fw-bold fontGray1">{userContext?.email}</p>

                <p className={`${styles.settingName}  mx-auto`}></p>
                {isEmailSend ? (
                  <button className={`${styles.sendEmailDone} bgWhite fs-4 ${styles.leftMargin}`}>
                    <FontAwesomeIcon icon={faEnvelope} /> 이메일이 전송되었습니다.
                  </button>
                ) : (
                  <button
                    className={`${styles.sendEmail} bgWhite fs-4 ${styles.leftMargin}`}
                    onClick={handlerAccountCloseEamil}
                  >
                    <FontAwesomeIcon icon={faEnvelope} /> 이메일 인증하기
                  </button>
                )}
                <p className="mt-3 text-muted small fontWhite">
                  ※ 이메일 인증 후 탈퇴가 완료됩니다.
                </p>
              </div>
            </div>
          </div>
        </>
      </div>
    </>
  );
}
