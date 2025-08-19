import React, { useState, useEffect } from "react";
import styles from "./NotificationPanel.module.css";
import Dummys from "../../utils/dummyData";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark } from "@fortawesome/free-solid-svg-icons";
import NotificationItem from "./NotificationItem";

type NotificationPanelProps = {
  isOpen: boolean;
  onClose: () => void;
};

export default function NotificationPanel({ isOpen, onClose }: NotificationPanelProps) {
  const [isMounted, setIsMounted] = useState(false);
  const [isAnimating, setIsAnimating] = useState(false);

  useEffect(() => {
    if (isOpen) {
      setIsMounted(true);
      const timer = setTimeout(() => {
        setIsAnimating(true);
      }, 10); // Mount and then animate
      return () => clearTimeout(timer);
    } else {
      setIsAnimating(false); // Start close animation
    }
  }, [isOpen]);

  const handleTransitionEnd = () => {
    if (!isOpen) {
      setIsMounted(false); // Unmount after close animation
    }
  };

  if (!isMounted) {
    return null;
  }

  return (
    <>
      <div className={`${styles.overlay} ${isAnimating ? styles.open : ""}`} onClick={onClose} />
      <div
        className={`${styles.panel} ${isAnimating ? styles.open : ""}`}
        onTransitionEnd={handleTransitionEnd}
      >
        <div className={styles.header}>
          <h2>알림</h2>
          <button onClick={onClose} className={styles.closeButton}>
            <FontAwesomeIcon icon={faXmark} width={24} height={24} />
          </button>
        </div>
        <div className={styles.notificationList}>
          {Dummys.Notifications.map((notification) => (
              <NotificationItem key={notification.id} notification={notification} />
          ))}

        </div>
      </div>
    </>
  );
}
