"use client";

import { useEffect, useState } from "react";
// components/SideBar.js
import styles from "./SideBar.module.css";
import { Nav, Image, Dropdown } from "react-bootstrap";
import CreatePopUp from "./CreatePopUp";
import { useUser } from "../contexts/UserContext";
import { BACKEND_API_URL } from "../utils/globalValues";
import SideBarLoading from "./SidebarLoading";

export default function SideBar() {
  const [isFeedCreaterOpen, setIsFeedCreaterOpen] = useState(false);
  const { userContext, fetchUserContext } = useUser();

  useEffect(() => {
    if (!userContext) {
      fetchUserContext();
    }
  }, [userContext]);

  if (!userContext) return <SideBarLoading />;

  const handleSetting = () => {};
  const handleCreateFeed = () => {
    setIsFeedCreaterOpen(true);
  };

  const handleLogout = async () => {
    await fetch(`${BACKEND_API_URL}/logout`, {
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

    window.location.href = "/login";
  };

  // 모든 탭에서 로그아웃 감지
  const bc = new BroadcastChannel("logout");
  bc.onmessage = (event) => {
    if (event.data === "logout") {
      window.location.href = "/login"; // 로그아웃된 상태로 리디렉션
    }
  };

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
            <Nav.Link href={`/profile/${userContext?.userId}`}>
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
            <Nav.Link href="#CopybaraLove">
              <Image src="/sidebar/like.svg" alt="Likes" className={styles.navImage} />
            </Nav.Link>
          </Nav>
        </div>

        {/* 설정 버튼 */}
        <div className={styles.settingsContainer}>
          <Dropdown drop="end">
            <Dropdown.Toggle
              variant="secondary"
              // className={styles.more}
              as="div"
              bsPrefix="custom-toggle"
            >
              <Image src="/sidebar/setting.svg" alt="setting" className={styles.settingImage} />
            </Dropdown.Toggle>
            <Dropdown.Menu className={styles.customDropdownMenu}>
              <Dropdown.Item href="/settings" onClick={handleSetting} className="fontGray4">
                설정
              </Dropdown.Item>
              <Dropdown.Divider />
              <Dropdown.ItemText className="fontGray1">개인정보처리방침</Dropdown.ItemText>
              <Dropdown.ItemText className="fontGray1">서비스 약관</Dropdown.ItemText>
              <Dropdown.Divider />
              <Dropdown.ItemText className="fontGray1">문제 신고</Dropdown.ItemText>
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
