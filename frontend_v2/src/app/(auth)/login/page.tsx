"use client";

import {
  ACCESS_TOKEN_FOR_VISITER,
  ACCESS_TOKEN_NAME,
  BACKEND_API_URL,
  FRONTEND_URL,
} from "@/app/(layout)/utils/globalValues";
import "../auth.css";

import { Image, OverlayTrigger, Stack, Tooltip } from "react-bootstrap";
import { useSearchParams } from "next/navigation";
import { useEffect, useState } from "react";

export default function Login() {
  const searchParams = useSearchParams();
  const until = searchParams.get("until");
  const status = searchParams.get("status"); // 쿼리 파라미터 확인
  const [message, setMessage] = useState<React.ReactNode>(null);

  useEffect(() => {
    if (status === "close-success") {
      setMessage(
        <>
          회원 탈퇴가 처리되었습니다. <br />
          14일 이내에 다시 로그인하면 계정이 복구됩니다. <br />
          <strong>{until} 자정 </strong>
          이후에는 복구가 불가능합니다.
        </>
      );
    } else if (status === "close-fail") {
      setMessage("회원 탈퇴 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
    } else if (status === "inactive-success") {
      setMessage(
        <>
          계정이 비활성화되었습니다.
          <br />
          다시 로그인하시면 자동으로 계정이 활성화 됩니다.
        </>
      );
    } else if (status === "inactive-fail") {
      setMessage("계정 비활성화에 실패했습니다. 다시 시도해주세요.");
    } else if (status === "SUSPENDED") {
      setMessage("이 계정은 정지된 상태입니다. 로그인할 수 없습니다.");
    } else {
      setMessage(null);
    }
  }, [status]);

  const handlerVisit = () => {
    localStorage.setItem(ACCESS_TOKEN_NAME, ACCESS_TOKEN_FOR_VISITER || "");
    window.location.href = FRONTEND_URL;
  };

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4"></p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}

      <Stack gap={4} className="col-md-5 mx-auto login-box pt-8">
        <Image
          src="/logoLong.svg"
          alt="Logo"
          className="login-btn title m-0 p-0 border-0 mx-auto mb-4"
        />
        <div className="text">
          <h4 className="login-btn fontGray4  text">간편 로그인으로</h4>
          <h4 className="login-btn fontGray4 text">서비스 시작하기</h4>
        </div>
        <div className="line mt-4 mb-3"></div>

        <OverlayTrigger
          key={"top"}
          placement={"top"}
          overlay={
            <Tooltip id={`tooltip-${"top"}`}>
              수집하는 개인정보 항목 : <strong>{"이메일"}</strong>
            </Tooltip>
          }
        >
          <a className="imageLink mx-auto" href={`${BACKEND_API_URL}/oauth2/authorization/google`}>
            <Image className="image" src="/googleLogin.svg" alt="LogInBtn" />
          </a>
        </OverlayTrigger>

        <OverlayTrigger
          key={"bottom"}
          placement={"bottom"}
          overlay={
            <Tooltip id={`tooltip-${"bottom"}`}>
              <strong>{"로그인 없이"}</strong> 둘러보기!
            </Tooltip>
          }
        >
          <a className="imageLink2 mx-auto" role="button" onClick={handlerVisit}>
            <p className="imageText">둘러보기</p>
          </a>
        </OverlayTrigger>
      </Stack>
      {message && (
        <div className="login-box-arelt mt-5 col-md-5 mx-auto mt-4 text-center">
          <div className="my-4 " role="alert">
            {message}
          </div>
        </div>
      )}
    </>
  );
}
