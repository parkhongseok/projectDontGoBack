import React from "react";
import styles from "./NotificationItem.module.css";
import { Image, OverlayTrigger, Tooltip } from "react-bootstrap";
import * as Types from "../../utils/types";
import Link from "next/link";

type NotificationItemProps = {
  notification: Types.Notification;
};

export default function NotificationItem({ notification }: NotificationItemProps) {
  const { userId, userImage, userName, userType, action, createdAt } = notification;

  const typeClass = styles[userType] || "";

  return (
    <>
      <OverlayTrigger
        key={notification.id} // key를 고유한 값으로 여기에 설정합니다.
        placement={"bottom"}
        overlay={
          <Tooltip id={`tooltip-${notification.id}`}>
            <strong>{"알림은 목업 입니다."}</strong> <br />곧 정식 지원 예정입니다.
          </Tooltip>
        }
      >
        {/* 자식 컴포넌트에는 key를 전달할 필요가 없습니다. */}
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
              <Link className="px-5" href={`/profile/${userId}`} legacyBehavior>
                <strong className={`${styles.userName} ${typeClass} cusorPointer mb-0`}>
                  {userName}
                </strong>
              </Link>
              {action}
            </p>
            <span className={styles.time}>{createdAt}</span>
          </div>
        </div>
      </OverlayTrigger>
    </>
  );
}
