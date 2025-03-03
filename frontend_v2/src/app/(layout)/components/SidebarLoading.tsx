"use client";
import styles from "./SideBar.module.css";
import { Nav, Image } from "react-bootstrap";

export default function SideBarLoading() {
  return (
    <>
      <div className={styles.sidebar}>
        {/* 로고 */}
        <div className={styles.logo}>
          <Nav.Link href="/">
            <Image src="/sidebar/logo.svg" alt="Logo" className={styles.logoImage} />
          </Nav.Link>
        </div>
        {/* 네비게이션 메뉴 */}
        <div className={`${styles.navContainer} mb-1`}>
          <Nav defaultActiveKey="/" className="flex-column">
            <Nav.Link href={`#/profile`}>
              <Image src="/sidebar/profile.svg" alt="profile" className={styles.navImage} />
            </Nav.Link>
            <Nav.Link href="#write">
              <Image src="/sidebar/plus.svg" alt="Write" className={styles.navImage} />
            </Nav.Link>
            <Nav.Link href="#/test">
              <Image src="/sidebar/like.svg" alt="Likes" className={styles.navImage} />
            </Nav.Link>
          </Nav>
        </div>
        <Image src="/sidebar/setting.svg" alt="setting" className={`${styles.settingImage} mb-3`} />
      </div>
    </>
  );
}
