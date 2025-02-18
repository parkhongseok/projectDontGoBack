'use client'

import "@/app/(auth)/auth.css";
import Link from "next/link";
import {Image } from 'react-bootstrap';

export default function Login() {

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4">Log In</p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`login-box`}>
        <Link href="http://localhost:8090/oauth2/authorization/google" className={`login-btn`} >
          <Image src="/googleLogin.svg" alt="LogInBtn"/>
        </Link>
      </div>
    </>
  );
}
