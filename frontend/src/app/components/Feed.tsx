import "../globals.css";
import styles from "./Feed.module.css"

import 'bootstrap/dist/css/bootstrap.min.css';
import {Stack} from 'react-bootstrap';

type TypeOfFeed = {
  userId: number;
  userName: string;
  feedType: string;
  beforeTime: string;
  content: string;
  likeCount: number;
  commentCount: number;
};

type FeedProps = {
  feed: TypeOfFeed; // Props의 타입 정의
};

export default function Feed({ feed } : FeedProps){
  // post.module.css에서 []로 검색한 class의 주소를 반환, 없다면 ""
  const feedTypeClass = styles[feed.feedType] || "";

  return (
    <Stack className="px-5" gap={3}>
      <Stack direction="horizontal" gap={3} >
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>
            {feed.userName}
          </p>
        </div>
        <div className="vr" />
        <div className="">        
          <p className={styles.time}>
            {feed.beforeTime}
          </p>
        </div>
        <div className="ms-auto"> 
          <button className={styles.more}>
            . . .
          </button>
        </div>
      </Stack>
      <div className="px-5">
        <p className={styles.content}>
          {feed.content}
        </p>
      </div>
      <Stack className="px-5" direction="horizontal" gap={3}>
        <div className="">
          <p className={styles.like}>
            좋아요 {feed.likeCount}개
          </p>
        </div>
        <div className="vr" />
        <div className="">
          <p className={styles.comment}>
            댓글 {feed.commentCount}개
          </p>
        </div>
      </Stack>
    </Stack>
  )
}


