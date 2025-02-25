"use client";

import "../globals.css";
import styles from "./Feed.module.css";
import { Stack } from "react-bootstrap";
import { useState } from "react";
import CreatePopUp from "./CreatePopUp";
import { useUser } from "../contexts/UserContext";

export default function CreateFeed() {
  const { userContext } = useUser();
  const [showWriteBox, setShowWriteBox] = useState(false);

  if (!userContext) {
    return <div className="loading" />;
  }

  const feedTypeClass = styles[userContext.userType] || "";
  const openWriteBox = () => {
    setShowWriteBox(true);
  };

  return (
    <>
      <Stack direction="horizontal" gap={3} className={`${styles.CreateFeedContainer}  cursorPointer px-2 py-2 b-4`}>
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>{userContext.userName}</p>
        </div>
        <div className="vr" />
        <div>
          <p className="fontGray1" onClick={openWriteBox}>
            글쓰기
          </p>
        </div>
        <div className="ms-auto">
          <button className={styles.write} onClick={openWriteBox}>
            게시
          </button>
        </div>
      </Stack>
      {showWriteBox ? <CreatePopUp setShowWriteBox={setShowWriteBox} /> : null}
    </>
  );
}
