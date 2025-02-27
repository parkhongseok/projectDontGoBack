"use client";

import { useState } from "react";
// components/SideBar.js
import styles from "./SideBar.module.css";
import { Nav, Image } from "react-bootstrap";
import CreatePopUp from "./CreatePopUp";

export default function SideBar() {
  const [isFeedCreaterOpen, setIsFeedCreaterOpen] = useState(false);
  const handleCreateFeed = () => {
    setIsFeedCreaterOpen(true);
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
        <div className="settingsContainer">
          <Nav.Link href="#profile">
            <Image src="/sidebar/setting.svg" alt="setting" className={styles.settingImage} />
          </Nav.Link>
        </div>
      </div>
    </>
  );
}
