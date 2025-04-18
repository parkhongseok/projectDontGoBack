"use client";

import "../../globals.css";
import styles from "../../components/Feed.module.css";
import { useUser } from "../../contexts/UserContext";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEnvelope } from "@fortawesome/free-solid-svg-icons/faEnvelope";
import { Stack } from "react-bootstrap";
import { BACKEND_API_URL } from "../../utils/globalValues";
// import * as Types from "../../utils/types";
import { httpRequest } from "../../utils/httpRequest";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function CloseAccount() {
  const [isEmailSend, setIsEmailSend] = useState(false);
  const { userContext } = useUser();
  const router = useRouter();
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
    httpRequest(method, url, body, success, fail);
  };

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">Settings</h5>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`feed-detail-container`}>
        {/* 본격 사용 가능 공간 */}
        <>
          <Stack direction="horizontal" className="mx-3 mt-3">
            <button
              className={`${styles.write} ${styles.exitBtn} custom-button`}
              onClick={handlerBefore}
            >
              이전 페이지
            </button>
          </Stack>
          <hr className="feed-underline fontGray4 mt-3" />
          <div className="">
            <div className=" overflow-hidden px-5">
              {/* 상단: 설명 영역 */}
              <div className="mx-5">
                <h2 className="fw-bold mt-3 mb-3 text-center fontRed">계정 삭제</h2>
                <p className=" text-center fontRed">
                  계정을 삭제하면, 모든 데이터가 영구적으로 삭제됩니다.
                  <br />
                  탈퇴를 진행하기 전에 반드시 아래의 사항을 확인해 주세요.
                </p>
                <ul className="mt-3 list-unstyled mx-5">
                  <li>✔ 작성한 게시글 및 댓글은 복구되지 않습니다.</li>
                  <li>✔ 이메일 인증을 통해 계속 진행하실 수 있습니다.</li>
                  <li>✔ 인증 후 2주가 지나면 탈퇴가 완료됩니다.</li>
                </ul>
              </div>
            </div>
            <hr className="feed-underline fontGray3 pb-4 mt-4" />
            <div className="mx-5 mb-5">
              {/* 하단: 버튼 영역 */}
              <div className="text-center rounded-4 bgGray4 py-5 ">
                <p className="mb-3 fontWhite px-5">
                  계정을 영구적으로 해지하고 싶다면 <br /> 마지막 단계를 안내하는 이메일을 다음
                  주소로 보내드리겠습니다.
                </p>

                <p className="mb-3 fw-bold fontGray1">{userContext?.email}</p>

                <p className={`${styles.settingName}  mx-auto`}></p>
                {isEmailSend ? (
                  <button className={`${styles.sendEmailDone} bgWhite fs-4 px-5 `}>
                    <FontAwesomeIcon icon={faEnvelope} /> 이메일이 전송되었습니다.
                  </button>
                ) : (
                  <button
                    className={`${styles.sendEmail} bgWhite fs-4 px-5 `}
                    onClick={handlerAccountCloseEamil}
                  >
                    <FontAwesomeIcon icon={faEnvelope} /> 이메일 인증하기
                  </button>
                )}
                <p className="mt-3 text-muted small fontWhite">
                  ※ 이메일 확인 후 탈퇴가 완료됩니다.
                </p>
              </div>
            </div>
          </div>
        </>
      </div>
    </>
  );
}
