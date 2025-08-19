"use client";

import "../../globals.css";
import styles from "../feeds/Feed.module.css";
import { Stack } from "react-bootstrap";
import * as Types from "../../utils/types";
import { useEffect, useState } from "react";
import { useUser } from "../../contexts/UserContext";
import CreateCommentPopUp from "./CreateCommentPopUp";

type userProps = {
  feed: Types.Feed | null;
};

export default function CreateComment({ feed }: userProps) {
  const [showWriteBox, setShowWriteBox] = useState(false);
  const { userContext } = useUser();

  if (!feed || !userContext) {
    return <div className={`${styles.CreateCommentContainer} cursorPointer px-4`} />;
  }
  const userTypeClass = styles[userContext.userType == "BLUE" ? "blueLight" : "redLight"] || "";
  const openWriteBox = () => {
    setShowWriteBox(true);
  };

  const isMyPost = feed?.userId == userContext?.userId;
  return (
    <div className={`${styles.CreateCommentContainer} cursorPointer px-4`}>
      <Stack direction="horizontal" gap={3}>
        <div>
          <p className={`${styles.userName} ${userTypeClass}`}>{userContext.userName}</p>
        </div>
        <div>
          <p className={`${styles.commentPlaceHolder} fontGray2`} onClick={openWriteBox}>
            {isMyPost ? `나에게` : `${feed.author} 님에게`} 답글 남기기 ...
          </p>
        </div>
      </Stack>
      {showWriteBox ? <CreateCommentPopUp setShowWriteBox={setShowWriteBox} feed={feed} /> : null}
    </div>
  );
}
