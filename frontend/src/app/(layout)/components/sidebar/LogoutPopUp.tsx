"use client";

import { Stack } from "react-bootstrap";

import "../../globals.css";
import styles from "../feeds/Feed.module.css";
import { usePathname } from "next/navigation";
import { httpRequest } from "../../utils/httpRequest";
import * as Types from "../../utils/types";
import { BACKEND_API_URL } from "../../utils/globalValues";

type propsType = {
  setIsLogoutPopUpOpen: React.Dispatch<React.SetStateAction<boolean>>;
};

export default function LogoutPopUp({ setIsLogoutPopUpOpen }: propsType) {
  const handleClosePopUp = () => {
    setIsLogoutPopUpOpen(false);
  };

  const handleLogout = async () => {
    await fetch(`${BACKEND_API_URL}/logout`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then((data) => console.log(data));

    const bc = new BroadcastChannel("logout");
    bc.postMessage("logout");

    window.location.href = "/login";
  };

  const bc = new BroadcastChannel("logout");
  bc.onmessage = (event) => {
    if (event.data === "logout") {
      window.location.href = "/login";
    }
  };

  return (
    <div className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}>
      <div className="sidebar-space" />
      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3"></p>
        {/* 본격 사용 가능 공간 */}
        <Stack gap={4} className={`${styles.deleteBoxContainer}  pt-4`}>
          <div className={` pt-2 fontBlack`}>
            <h5>정말 로그아웃 하시겠습니까?</h5>
          </div>
          <div className={`fontGray4 pb-2`}>흐엉</div>
          {/* 상단  취소 / 게시글 작성 / ... */}
          <Stack className={`${styles.deleteBtns} `} direction="horizontal">
            <div className={`${styles.deleteBtn} ms-auto `}>
              <button className={` fontGray4`} onClick={handleClosePopUp}>
                취소
              </button>
            </div>
            <div className={`${styles.deleteBtn} ${styles.deleteBtnLine} ms-auto `}>
              <button className={` fontRed `} onClick={handleLogout}>
                네
              </button>
            </div>
          </Stack>
        </Stack>
      </div>
    </div>
  );
}
