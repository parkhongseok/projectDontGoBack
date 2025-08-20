"use client";

import { useEffect, useState } from "react";
import styles from "./SideBar.module.css";
import { Nav, Image } from "react-bootstrap";
import CreatePopUp from "../feeds/CreatePopUp";
import { useUser } from "../../contexts/UserContext";
import SideBarLoading from "./SidebarLoading";
import NotificationPanel from "./NotificationPanel";
import SidebarSettingPanel from "./SidebarSettingPanel";

export default function SideBar() {
  const [isFeedCreaterOpen, setIsFeedCreaterOpen] = useState(false);
  const { userContext, fetchUserContext } = useUser();
  const [isNotificationPanelOpen, setIsNotificationPanelOpen] = useState(false);
  const [isSettingPanelOpen, setIsSettingPanelOpen] = useState(false);

  useEffect(() => {
    if (!userContext) {
      fetchUserContext();
    }
  }, [userContext]);

  useEffect(() => {
    const handleOverflow = () => {
      if ((isNotificationPanelOpen || isSettingPanelOpen) && window.innerWidth < 768) {
        document.body.style.overflow = 'hidden';
      } else {
        document.body.style.overflow = 'auto';
      }
    };

    handleOverflow();

    window.addEventListener('resize', handleOverflow);

    return () => {
      document.body.style.overflow = 'auto';
      window.removeEventListener('resize', handleOverflow);
    };
  }, [isNotificationPanelOpen, isSettingPanelOpen]);

  if (!userContext) return <SideBarLoading />;

  const handleCreateFeed = () => {
    setIsFeedCreaterOpen(true);
  };

  const handleToggleNotifications = (e: { preventDefault: () => void }) => {
    e.preventDefault();
    if (isSettingPanelOpen) {
      setIsSettingPanelOpen(false);
      requestAnimationFrame(() => {
        setIsNotificationPanelOpen(true);
      });
    } else {
      setIsNotificationPanelOpen(!isNotificationPanelOpen);
    }
  };

  const handleToggleSettings = () => {
    if (isNotificationPanelOpen) {
      setIsNotificationPanelOpen(false);
      requestAnimationFrame(() => {
        setIsSettingPanelOpen(true);
      });
    } else {
      setIsSettingPanelOpen(!isSettingPanelOpen);
    }
  };


  return (
    <>
      {isFeedCreaterOpen && <CreatePopUp setIsFeedCreaterOpen={setIsFeedCreaterOpen} />}

      <NotificationPanel
        isOpen={isNotificationPanelOpen}
        onClose={() => setIsNotificationPanelOpen(false)}
      />
      <SidebarSettingPanel
        isOpen={isSettingPanelOpen}
        onClose={() => setIsSettingPanelOpen(false)}
      />

      <div className={styles.sidebar}>
        <div className={styles.logo}>
          <Nav.Link href="/">
            <Image src="/sidebar/logo.svg" alt="Logo" className={styles.logoImage} />
          </Nav.Link>
        </div>

        <div className={styles.navContainer}>
          <Nav defaultActiveKey="/">
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
            <Nav.Link href="#CopybaraLove" onClick={handleToggleNotifications}>
              <Image src="/sidebar/like.svg" alt="Likes" className={styles.navImage} />
            </Nav.Link>
          </Nav>
        </div>

        <div className={styles.settingsContainer} onClick={handleToggleSettings}>
          <Image src="/sidebar/setting.svg" alt="setting" className={styles.settingImage} />
        </div>
      </div>
    </>
  );
}