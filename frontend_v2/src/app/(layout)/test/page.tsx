"use client";

import "../globals.css";
import ProfileSetting from "../components/profiles/ProfileSetting";
import { useState } from "react";

export default function Test() {
  const [isSettingOpen, setIsSettingOpen] = useState(false);
  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4">TEST</p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className="pt-4 feeds-container">
        <ProfileSetting setIsSettingOpen={setIsSettingOpen} />
      </div>
    </>
  );
}
