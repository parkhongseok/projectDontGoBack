import "../globals.css";
import styles from "./Feed.module.css"
import 'bootstrap/dist/css/bootstrap.min.css';
import {Dropdown, Stack} from 'react-bootstrap';
import * as Types  from '../types';

type CommentProps = {
  comment: Types.Comment;
};

export default function Feed({ comment } : CommentProps){

  const feedTypeClass = styles[comment.commentType] || "";

  return (
    <Stack className="px-5" gap={3}>
      <Stack direction="horizontal" gap={3} >
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>
            {comment.userName}
          </p>
        </div>
        <div className="vr" />
        <div className="">        
          <p className={styles.time}>
            {comment.beforeTime}
          </p>
        </div>
        <div className="ms-auto"> 
          <Dropdown >
            <Dropdown.Toggle className={styles.more} as="div" id="dropdown-basic" bsPrefix="custom-toggle">
            . . .
            </Dropdown.Toggle>

            <Dropdown.Menu>
              <Dropdown.Item href="#/action-1">수정하기</Dropdown.Item>
              <Dropdown.Item href="#/action-2">삭제하기</Dropdown.Item>
            </Dropdown.Menu>
          </Dropdown>

        </div>
      </Stack>
      <div className="px-5">
        <p className={styles.content}>
          {comment.content}
        </p>
      </div>
      <Stack className="px-5" direction="horizontal" gap={3}>
        <div className="">
          <p className={styles.like}>
            좋아요 {comment.likeCount}개
          </p>
        </div>
        <div className="vr" />
        <div className="">
          <p className={styles.comment}>
            댓글 {comment.commentCount}개
          </p>
        </div>
      </Stack>
    </Stack>
  )
}


