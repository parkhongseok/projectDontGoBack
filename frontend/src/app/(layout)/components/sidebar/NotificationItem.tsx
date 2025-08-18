// components/NotificationItem.js
import React from "react";
import styles from "./NotificationItem.module.css";
import { Image } from "react-bootstrap";
import * as Types from "../../utils/types";

type NotificationItemProps = {
  notification: Types.Notification;
};

export default function NotificationItem({ notification }: NotificationItemProps) {
  const { userImage, userName, action, createdAt } = notification;

  return (
    <div className={styles.item}>
      <Image
        src={userImage}
        alt={`${userName} profile`}
        roundedCircle
        className={styles.userImage}
        onError={(e: React.SyntheticEvent<HTMLImageElement, Event>) => {
          (e.target as HTMLImageElement).onerror = null;
          (e.target as HTMLImageElement).src = "https://placehold.co/40x40/EFEFEF/333?text=U";
        }}
      />
      <div className={styles.content}>
        <p className={styles.text}>
          <strong>{userName}</strong>
          {action}
        </p>
        <span className={styles.time}>{createdAt}</span>
      </div>
    </div>
  );
}
