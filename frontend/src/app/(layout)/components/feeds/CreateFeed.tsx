"use client";

import "../../globals.css";
import styles from "./Feed.module.css";
import { Stack } from "react-bootstrap";
import { useState } from "react";
import CreatePopUp from "./CreatePopUp";
import { useUser } from "../../contexts/UserContext";
import Link from "next/link";

export default function CreateFeed() {
  const { userContext } = useUser();
  const [isFeedCreaterOpen, setIsFeedCreaterOpen] = useState(false);

  if (!userContext) {
    return <div className="loading" />;
  }

  const feedTypeClass = styles[userContext.userType] || "";
  const handleCreateFeed = () => {
    setIsFeedCreaterOpen(true);
  };

  return (
    <>
      <Stack
        direction="horizontal"
        gap={3}
        className={`${styles.CreateFeedContainer}  cursorPointer px-2 py-2 `}
      >
        <div>
          <Link href={`/profile/${userContext?.userId}`}>
            <p className={`${styles.userName} ${feedTypeClass} ms-3`}>{userContext.userName}</p>
          </Link>
        </div>
        <div className="vr" />
        <div>
          <p className={`${styles.createTextholder}`} onClick={handleCreateFeed}>
            글쓰기
          </p>
        </div>
        <div className="ms-auto">
          <button className={`${styles.write} custom-button`} onClick={handleCreateFeed}>
            게시
          </button>
        </div>
      </Stack>
      {isFeedCreaterOpen ? <CreatePopUp setIsFeedCreaterOpen={setIsFeedCreaterOpen} /> : null}
    </>
  );
}
