'use client'

import "../globals.css";
import styles from "./Feed.module.css"
import {Stack} from 'react-bootstrap';

type TypeOfCreateFeed = {
  userId: number;
  userName: string;
  feedType: string;
};

type userProps = {
  user : TypeOfCreateFeed; // Props의 타입 정의
  setShowWriteBox : React.Dispatch<React.SetStateAction<boolean>>
};

export default function CreateFeed({ user , setShowWriteBox} : userProps){
  const feedTypeClass = styles[user.feedType] || "";
  const openWriteBox = () => {
    setShowWriteBox(true)
  };
  
  return (
    <div className={`${styles.writeFeedContainer} px-5`}>
      <Stack direction="horizontal" gap={3} >
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
    </div>
  )
}


