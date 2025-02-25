"use client";

import "../auth.css";
import Link from "next/link";
import { Image, Stack } from "react-bootstrap";

export default function Login() {
  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4"></p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <Stack as="div" className="login-box mt-5 ">
        <h1 className="login-btn fontGray4 title mb-4 ">Dont Go Back</h1>
        <div className="text">
          <h4 className="login-btn fontGray4  text">간편 로그인으로</h4>
          <h4 className="login-btn fontGray4 text">서비스 시작하기</h4>
        </div>
        <div className="line foot">
          <Link className="imageLink" href="http://localhost:8090/oauth2/authorization/google">
            <Image className="image" src="/googleLogin.svg" alt="LogInBtn" />
          </Link>
        </div>
      </Stack>
    </>
  );
}
