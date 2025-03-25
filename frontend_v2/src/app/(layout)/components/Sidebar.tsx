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
    // 액세스 토큰 삭제
    localStorage.removeItem("access_token");

    // 백엔드에 로그아웃 요청 (서버에서 리프래시 토큰 검증 후, 무효한 토큰을 브라우저에 발급)
    // get방식이 아닌 이유는
    // 1. 쿠키는 사용자가 명시적으로 보내는 요청이라서, 쿠키가 포함된다. (리프래시 토큰 재발급의 경우, 자동 요청이라 get방식으로 지정해야, 다른 프로세스 간에 쿠키 전달 가능했음)
    // 2. get방식은 조회의 의도, POST는 행위(서버에서 무언가 일어남)의 목적
    // 이에 맞춰 브라우저 내에서도 GET방식의 경우 미리 요청을 보내는 등 의도치않은 동작을 수반할 수 있고, CSRF 방어 대상이 아님
    // 따라서 POST 가 굳
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
              <Dropdown.ItemText onClick={handleSetting} className="fontGray1">
                설정
              </Dropdown.ItemText>
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
