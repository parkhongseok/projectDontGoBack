'use client'

import "../globals.css";
import styles from "./Feed.module.css"
import {Stack} from 'react-bootstrap';
import * as Types from '../utils/types'
import CreateBox from "./CreateBox";
import { useState } from "react";
import { useUser } from "../contexts/UserContext";

type userProps = {
  feed : Types.Feed | null;
};

export default function CreateComment({feed} : userProps){
  const [showWriteBox, setShowWriteBox] = useState(false);
  const { userContext } = useUser();

  if (!feed || !userContext ) {
    return <div className={`${styles.CreateCommentContainer} cursorPointer px-4`}/> ;
  }
  const feedTypeClass = styles[(userContext.userType == "BLUE") ? "blueLight" : "redLight"] || "";
  const openWriteBox = () => {
    setShowWriteBox(true)
  };

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
        showWriteBox ? <CreateBox setShowWriteBox = {setShowWriteBox} /> : null
      }
    </div>
  )
}


