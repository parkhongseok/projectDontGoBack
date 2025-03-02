"use client";

import { useState } from "react";
// components/SideBar.js
import styles from "./SideBar.module.css";
import { Nav, Image, Dropdown } from "react-bootstrap";
import CreatePopUp from "./CreatePopUp";

export default function SideBar() {
  const [isFeedCreaterOpen, setIsFeedCreaterOpen] = useState(false);
  const handleCreateFeed = () => {
    setIsFeedCreaterOpen(true);
  };

  const handleLogout = async () => {
    // 액세스 토큰 삭제
    localStorage.removeItem("access_token");
    localStorage.removeItem("feedContext");

    // 백엔드에 로그아웃 요청 (서버에서 리프래시 토큰 검증 후, 무효한 토큰을 브라우저에 발급)
    await fetch("http://localhost:8090/api/logout", {
      method: "POST",
      credentials: "include", // 쿠키 자동 전송
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then((data) => console.log(data));

    // 다른 탭에도 로그아웃 이벤트 전파
    const bc = new BroadcastChannel("logout");
    bc.postMessage("logout");

    window.history.replaceState(null, "", "/");
    window.location.href = "/login";
  };

  // 모든 탭에서 로그아웃 감지
  const bc = new BroadcastChannel("logout");
  bc.onmessage = (event) => {
    if (event.data === "logout") {
      localStorage.removeItem("access_token");
      window.location.href = "/login"; // 로그아웃된 상태로 리디렉션
    }
  };

  const handleSetting = () => {};

  return (
    <>
      {isFeedCreaterOpen && <CreatePopUp setIsFeedCreaterOpen={setIsFeedCreaterOpen} />}
      <div className={styles.sidebar}>
        {/* 로고 */}
        <div className={styles.logo}>
          <Nav.Link href="/">
            <Image src="/sidebar/logo.svg" alt="Logo" className={styles.logoImage} />
          </Nav.Link>
        </div>
        {/* 네비게이션 메뉴 */}
        <div className={styles.navContainer}>
          <Nav defaultActiveKey="/" className="flex-column">
            <Nav.Link href="/login">
              <Image src="/sidebar/profile.svg" alt="profile" className={styles.navImage} />
            </Nav.Link>
            <Nav.Link href="#write">
              <Image
                src="/sidebar/plus.svg"
                alt="Write"
                className={styles.navImage}
                onClick={handleCreateFeed}
              />
            </Nav.Link>
            <Nav.Link href="/test">
              <Image src="/sidebar/like.svg" alt="Likes" className={styles.navImage} />
            </Nav.Link>
          </Nav>
        </div>

        {/* 설정 버튼 */}
        <div className={styles.settingsContainer}>
          <Dropdown>
            <Dropdown.Toggle
              variant="secondary"
              // className={styles.more}
              as="div"
              bsPrefix="custom-toggle"
            >
              <Image src="/sidebar/setting.svg" alt="setting" className={styles.settingImage} />
            </Dropdown.Toggle>
            <Dropdown.Menu>
              <Dropdown.Item onClick={handleSetting}>설정</Dropdown.Item>
              <Dropdown.Divider />
              <Dropdown.Item>문제 신고</Dropdown.Item>
              <Dropdown.Item onClick={handleLogout}>
                <span className={styles.logout}>로그아웃</span>
              </Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </div>
    </>
  );
}
