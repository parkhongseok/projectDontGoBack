'use client'

import "../globals.css";
import styles from "./Feed.module.css"
import {Stack} from 'react-bootstrap';
import Types from '../types'
import CreateBox from "./CreateBox";
import { useState } from "react";

type userProps = {
  feed : Types.Feed | null;
  user : Types.User; // Props의 타입 정의
};

export default function CreateComment({feed, user} : userProps){

  if (!feed) {
    return <div className={`${styles.CreateCommentContainer} cursorPointer px-4`}/> ;
  }
  const feedTypeClass = styles[(feed.feedType == "blue") ? "blueLight" : "redLight"] || "";
  const openWriteBox = () => {
    setShowWriteBox(true)
  };
  const [showWriteBox, setShowWriteBox] = useState(false);

  return (
    <div className={`${styles.CreateCommentContainer} cursorPointer px-4`}>
      <Stack direction="horizontal" gap={3} >
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>
            {feed.userName}
          </p>
        </div>
        <div >
          <p className={`${styles.commentPlaceHolder} fontGray2`} onClick={openWriteBox}>
            님에게 답글 남기기 ...
          </p>
        </div>
      </Stack>
      {
        showWriteBox ? <CreateBox user = {user} setShowWriteBox = {setShowWriteBox} /> : null
      }
    </div>
  )
}


