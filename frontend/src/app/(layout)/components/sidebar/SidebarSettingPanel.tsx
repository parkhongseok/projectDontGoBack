import React, { useState, useEffect } from "react";
import styles from "./SidebarSettingPanel.module.css";
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
import PolicyPopup from "../policy/PolicyPopup";
import PrivacyPolicyContent from "../policy/PrivacyPolicyContent";
import TermsOfServiceContent from "../policy/TermsOfServiceContent";
import ReportProblemPopup from "../report/ReportProblemPopup";

type SidebarSettingPanelProps = {
  isOpen: boolean;
  onClose: () => void;
};

export default function SidebarSettingPanel({ isOpen, onClose }: SidebarSettingPanelProps) {
  const [isMounted, setIsMounted] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);
  const [isLogoutPopUpOpen, setIsLogoutPopUpOpen] = useState(false);
  const [isPolicyPopupOpen, setIsPolicyPopupOpen] = useState(false);
  const [isTermsPopupOpen, setIsTermsPopupOpen] = useState(false);
  const [isReportProblemPopupOpen, setIsReportProblemPopupOpen] = useState(false);

  

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

  const handlePolicyPopup = () => {
    setIsPolicyPopupOpen(true);
  };

  const handleTermsPopup = () => {
    setIsTermsPopupOpen(true);
  };

  const handleReportProblemPopup = () => {
    setIsReportProblemPopupOpen(true);
  };

  return (
    <>
      {isLogoutPopUpOpen && <LogOutPopUp setIsLogoutPopUpOpen={setIsLogoutPopUpOpen} />}
      {isPolicyPopupOpen && (
        <PolicyPopup title="개인정보처리방침" onPolicyClose={() => setIsPolicyPopupOpen(false)}>
          <PrivacyPolicyContent />
        </PolicyPopup>
      )}
      {isTermsPopupOpen && (
        <PolicyPopup title="서비스 약관" onPolicyClose={() => setIsTermsPopupOpen(false)}>
          <TermsOfServiceContent />
        </PolicyPopup>
      )}
      {isReportProblemPopupOpen && (
        <ReportProblemPopup onClose={() => setIsReportProblemPopupOpen(false)} />
      )}
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
        <div className={`${styles.content}`}>
          <Nav className="flex-column">
            {/* 계정 관리 */}
            <Nav.Link
              href="/settings"
              className={`${itemStyles.item} d-flex justify-content-between align-items-center px-5 p-4`}
            >
              <FontAwesomeIcon icon={faUserGear} className={styles.navLink} />
              <strong className={styles.navLink}>계정 관리</strong>
            </Nav.Link>
            {/* 개인정보처리방침 */}
            <Nav.Link
              onClick={handlePolicyPopup}
              className={`${itemStyles.item} d-flex justify-content-between align-items-center px-5 p-4`}
            >
              <FontAwesomeIcon icon={faShieldHalved} className={styles.navLink} />
              <span className={styles.navLink}>개인정보처리방침</span>
            </Nav.Link>

            {/* 서비스 약관 */}
            <Nav.Link
              onClick={handleTermsPopup}
              className={`${itemStyles.item} d-flex justify-content-between align-items-center px-5 p-4`}
            >
              <FontAwesomeIcon icon={faFileLines} className={styles.navLink} />
              <span className={styles.navLink}>서비스 약관</span>
            </Nav.Link>

            {/* 문제 신고 */}
            <Nav.Link
              onClick={handleReportProblemPopup}
              className={`${itemStyles.item} d-flex justify-content-between align-items-center px-5 p-4`}
            >
              <FontAwesomeIcon icon={faTriangleExclamation} className={styles.navLink} />
              <span className={styles.navLink}>문제 신고</span>
            </Nav.Link>
          </Nav>

          {/* Log out */}
          <div
            onClick={handleLogoutPopup}
            className={`${itemStyles.item} ${styles.footer} ${styles.footerText} p-4 d-flex justify-content-between align-items-center px-5`}
          >
            <FontAwesomeIcon icon={faArrowRightFromBracket} className={styles.icon} />
            <span className={`${styles.footerText}`}>로그아웃</span>
          </div>
        </div>
      </div>
    </>
  );
}
