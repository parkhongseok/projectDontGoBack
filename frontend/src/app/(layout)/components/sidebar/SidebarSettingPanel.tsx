import React, { useState, useEffect } from "react";
import styles from "./SidebarSettingPanel.module.css"; // Changed import
import itemStyles from "./NotificationItem.module.css";
import { Nav } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faXmark,
  faUserGear,
  faShieldHalved,
  faFileLines,
  faTriangleExclamation,
  faArrowRightFromBracket,
} from "@fortawesome/free-solid-svg-icons";
import LogOutPopUp from "./LogoutPopUp";

type SidebarSettingPanelProps = {
  isOpen: boolean;
  onClose: () => void;
};

export default function SidebarSettingPanel({ isOpen, onClose }: SidebarSettingPanelProps) {
  const [isMounted, setIsMounted] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [isLogoutPopUpOpen, setIsLogoutPopUpOpen] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsMounted(true);
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10);
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false);
    }
  }, [isOpen]);

  const handleTransitionEnd = () => {
    if (!isOpen) {
      setIsMounted(false);
    }
  };

  if (!isMounted) {
    return null;
  }

  const handleLogoutPopup = () => {
    setIsLogoutPopUpOpen(true);
  };

  return (
    <>
      {isLogoutPopUpOpen && <LogOutPopUp setIsLogoutPopUpOpen={setIsLogoutPopUpOpen} />}
      <div className={`${styles.overlay} ${isAnimating ? styles.open : ""}`} onClick={onClose} />
      <div
        className={`${styles.panel} ${isAnimating ? styles.open : ""}`}
        onTransitionEnd={handleTransitionEnd}
      >
        <div className={styles.header}>
          <h2>설정</h2>
          <button onClick={onClose} className={styles.closeButton}>
            <FontAwesomeIcon icon={faXmark} width={24} height={24} />
          </button>
        </div>
        {/* Panel content */}
        <div className={styles.content}>
          <Nav className="flex-column">
            {/* 계정 관리 */}
            <Nav.Link href="/settings" className={`${itemStyles.item}`}>
              <div className={`${itemStyles.item}`}>
                <div className={itemStyles.content}>
                  {<FontAwesomeIcon icon={faUserGear} className={styles.navLink} />}
                </div>
                <div>
                  <p className={`${itemStyles.content} ${styles.navLink}`}>계정 관리</p>
                </div>
              </div>
            </Nav.Link>

            {/* 개인정보처리방침 */}

            <Nav.Link href="#" className={`${itemStyles.item}`}>
              <div className={`${itemStyles.item}`}>
                <div className={itemStyles.content}>
                  <FontAwesomeIcon icon={faShieldHalved} className={styles.navUnLink} />
                </div>
                <div>
                  <p className={`${itemStyles.content} ${styles.navUnLink}`}>개인정보처리방침</p>
                </div>
              </div>
            </Nav.Link>

            {/* 서비스 약관 */}
            <Nav.Link href="#" className={`${itemStyles.item}`}>
              <div className={`${itemStyles.item}`}>
                <div className={itemStyles.content}>
                  <FontAwesomeIcon icon={faFileLines} className={styles.navUnLink} />
                </div>
                <div>
                  <p className={`${itemStyles.content} ${styles.navUnLink}`}>서비스 약관</p>
                </div>
              </div>
            </Nav.Link>

            {/* 문제 신고 */}
            <Nav.Link href="#" className={`${itemStyles.item}`}>
              <div className={`${itemStyles.item}`}>
                <div className={itemStyles.content}>
                  <FontAwesomeIcon icon={faTriangleExclamation} className={styles.navUnLink} />
                </div>
                <div>
                  <p className={`${itemStyles.content} ${styles.navUnLink}`}>문제 신고</p>
                </div>
              </div>
            </Nav.Link>
          </Nav>

          {/* Log out */}
          <div
            onClick={handleLogoutPopup}
            className={`${itemStyles.item} ${styles.footer} ${styles.footerText} p-4`}
          >
            <div className={`${itemStyles.content} px-4`}>
              <FontAwesomeIcon icon={faArrowRightFromBracket} className={styles.icon} />
            </div>
            <div>
              <p className={`${itemStyles.content} ${styles.footerText} px-4`}>로그 아웃</p>
            </div>
          </div>

          {/* <div className={`${itemStyles.item} pt-3`}>
            <Nav.Link onClick={onLogout} className={` ${styles.logout}`}>
              <FontAwesomeIcon icon={faArrowRightFromBracket} className={styles.icon} />{" "}
              <p className={`${styles.footerText}`}>로그 아웃</p>
            </Nav.Link>
          </div> */}
        </div>
      </div>
    </>
  );
}
