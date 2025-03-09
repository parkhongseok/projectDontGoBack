"use client";

import { ACCESS_TOKEN_NAME } from "@/app/(layout)/utils/values";
import "../auth.css";
import Link from "next/link";
import { Image, OverlayTrigger, Stack, Tooltip } from "react-bootstrap";

export default function Login() {
  const access_token =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGVtYWlsLmNvbSIsImlhdCI6MTc0MDY2OTQ3MSwiZXhwIjoxNzQxODc5MDcxfQ.J8O74yZaap-ptorzzoAYVIRqQAd8mZXgUPg9iDH6GR8";
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
        <Link
          className="imageLink mx-auto"
          href="http://localhost:8090/oauth2/authorization/google"
        >
          <Image className="image" src="/googleLogin.svg" alt="LogInBtn" />
        </Link>
        <OverlayTrigger
          key={"bottom"}
          placement={"bottom"}
          overlay={
            <Tooltip id={`tooltip-${"bottom"}`}>
              <strong>{"로그인 없이"}</strong> 둘러보기!
            </Tooltip>
          }
        >
          <Link
            className="imageLink2 mx-auto"
            href={`http://localhost:3000/?${ACCESS_TOKEN_NAME}=${access_token}`}
          >
            <p className="imageText">둘러보기</p>
          </Link>
        </OverlayTrigger>
      </Stack>
    </>
  );
}
