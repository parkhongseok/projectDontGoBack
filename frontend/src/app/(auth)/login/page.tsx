'use client'

import "@/app/(auth)/auth.css";
import Link from "next/link";
import {Image, Stack } from 'react-bootstrap';

export default function Login() {

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4"></p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <Stack as="div" className="login-box ">
        <div className="empty "></div>
        <h1 className="login-btn fontGray4 title">
          LOGIN
        </h1>
        <h4 className="login-btn fontGray4 mb-5 ">
        서비스 이용을 위해 로그인을 해주세요!
        </h4>
        <Link className="mt-4"  href="http://localhost:8090/oauth2/authorization/google"  >
          <Image className="image" src="/googleLogin.svg" alt="LogInBtn"/>
        </Link>
        <div className="empty"></div>

      </Stack>
    </>
  );
}
