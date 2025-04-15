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

export default function CloseAccount() {
  const { userContext } = useUser();

  const handlerCloseAccount = () => {
    const method = "POST";
    const url = `${BACKEND_API_URL}/v1/users/account-close-request`;
    const body = null;
    const success = () =>
      // result: Types.ResData<{ feedId: number; content: string; updatedAt: string }>
      {
        alert("이메일을 확인해 주세요! 탈퇴 링크가 전송되었습니다.");
      };
    const fail = () => {
      alert("탈퇴 요청에 실패했습니다. 다시 시도해 주세요.");
    };
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
              // onClick={handlerClose}
            >
              이전 페이지
            </button>
          </Stack>
          <hr className="feed-underline fontGray4 mt-3" />
          <div className="">
            <div className=" overflow-hidden px-5">
              {/* 상단: 설명 영역 */}
              <div className="mx-5">
                <h2 className="fw-bold mt-3 mb-3 text-center">계정 탈퇴</h2>
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
            <hr className="feed-underline fontGray4 pb-4 mt-4" />
            <div className="mx-5 mb-5">
              {/* 하단: 버튼 영역 */}
              <div className="text-center rounded-4 bg-white py-5 ">
                <p className="mb-3 fontGray4 px-5">
                  계정을 영구적으로 해지하고 싶다면 <br /> 마지막 단계를 안내하는 이메일을 다음
                  주소로 보내드리겠습니다.
                </p>

                <p className="mb-3 fw-bold fontGray2">{userContext?.email}</p>

                <p className={`${styles.settingName}  mx-auto`}></p>

                <button className={`${styles.write} fs-4 px-5 `} onClick={handlerCloseAccount}>
                  <FontAwesomeIcon icon={faEnvelope} /> 탈퇴 이메일 보내기
                </button>
                <p className="mt-3 text-muted small">※ 이메일 확인 후 탈퇴가 완료됩니다.</p>
              </div>
            </div>
          </div>
        </>
      </div>
    </>
  );
}
