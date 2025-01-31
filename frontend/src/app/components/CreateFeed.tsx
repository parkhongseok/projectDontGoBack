'use client'

import "../globals.css";
import styles from "./Feed.module.css"
import {Stack} from 'react-bootstrap';
import * as Types from '../types'
import { useState } from "react";
import CreateBox from "./CreateBox";

type userProps = {
  user : Types.User;
};

export default function CreateFeed({ user } : userProps ){
  const feedTypeClass = styles[user.userType] || "";
  const openWriteBox = () => {
    setShowWriteBox(true)
  };
  const [showWriteBox, setShowWriteBox] = useState(false);

  return (
    < >
      <Stack direction="horizontal" gap={3} className={`${styles.CreateFeedContainer}  cursorPointer px-2 py-2 b-4`} >
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>
            {user.userName}
          </p>
        </div>
        <div className="vr" />
        <div >
          <p className='fontGray1' onClick={openWriteBox}>
            글쓰기
          </p>
        </div>
        <div className="ms-auto"> 
          <button className={styles.write} onClick={openWriteBox}>
            게시
          </button>
        </div>
      </Stack>
      {
        showWriteBox ? <CreateBox setShowWriteBox = { setShowWriteBox }/> : null
      }
    </>
  )
}


