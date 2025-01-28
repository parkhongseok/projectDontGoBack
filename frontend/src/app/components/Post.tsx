import "../globals.css";
import styles from "./Post.module.css"

import 'bootstrap/dist/css/bootstrap.min.css';
import {Stack} from 'react-bootstrap';

type TypeOfPost = {
  userId: number;
  userName: string;
  postType: string;
  beforeTime: string;
  content: string;
  likeCount: number;
  commentCount: number;
};

type PostProps = {
  postOne: TypeOfPost; // Props의 타입 정의
};

export default function Post({ postOne } : PostProps){
  // post.module.css에서 []로 검색한 class의 주소를 반환, 없다면 ""
  const postTypeClass = styles[postOne.postType] || "";

  return (
    <Stack className="px-5" gap={3}>
      <Stack direction="horizontal" gap={3} >
        <div className={styles.post}>
          <h3 className={`${styles.userName} ${postTypeClass}`}>
            {postOne.userName}
          </h3>
        </div>
        <div className="vr" />
        <div className="">        
          <h3 className={styles.time}>
            {postOne.beforeTime}
          </h3>
        </div>
        <div className="ms-auto"> 
          <button className={styles.more}>
            ...
          </button>
        </div>
      </Stack>
      <div className="px-5">
        <p className={styles.content}>
          {postOne.content}
        </p>
      </div>
      <Stack className="px-5" direction="horizontal" gap={3}>
        <div className="">
          <p className={styles.like}>
            좋아요 {postOne.likeCount}개
          </p>
        </div>
        <div className="vr" />
        <div className="">
          <p className={styles.comment}>
            댓글 {postOne.commentCount}개
          </p>
        </div>
      </Stack>
    </Stack>
  )
}


