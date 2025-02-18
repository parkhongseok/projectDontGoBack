import "../globals.css";
import styles from "./Feed.module.css"
import 'bootstrap/dist/css/bootstrap.min.css';
import {Dropdown, Stack} from 'react-bootstrap';
import * as Types from '../utils/types';
import Link from "next/link";
import { useFeed } from "../contexts/FeedContext";
import { useParams } from "next/navigation";
import { useState } from "react";
import EditBox from "./EditBox";
import DeleteBox from "./DeleteBox";
import { useUser } from "../contexts/UserContext";

type FeedProps = {
  feed: Types.Feed | null;  // Props의 타입 정의
};

export default function Feed({ feed } : FeedProps){
  const { userContext } = useUser();
  const { updateFeedContext } = useFeed();
  const { id } = useParams<{ id: string }>();
  const [ showEditBox, setShowEditBox ] = useState(false);
  const [ showDeleteBox, setShowDeleteBox ] = useState(false);
  const openEditBox = () => {
    setShowEditBox(true);
  }
  const openDeleteBox = () => {
    setShowDeleteBox(true);
  }

  if (!feed || !userContext) {
    return <div className="loading"/>;  // feed가 없으면 로딩 중인 상태 표시 공간 컴포넌트로 대체 고민
  }
  
  // post.module.css에서 []로 검색한 class의 주소를 반환, 없다면 ""
  const feedTypeClass = styles[feed.feedType] || "";
  const handleFeedClick = () => {
    updateFeedContext(feed);
  };

  return (
    <Stack className="px-5" gap={3}>
      {
        showEditBox &&
        <div >
          <EditBox setShowEditBox = { setShowEditBox }/> 
        </div>
      }
      {
        showDeleteBox &&
        <DeleteBox FeedId={feed.feedId} setShowDeleteBox={setShowDeleteBox}/>
      }
      <Stack direction="horizontal" gap={3} >
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>
            {feed.userName}
          </p>
        </div>
        <div className="vr" />
        <div className="">        
          <p className={styles.time}>
            {feed.createdAt}
          </p>
        </div>
        <div className="ms-auto"> 
          <Dropdown >
            <Dropdown.Toggle className={styles.more} as="div" id="dropdown-basic" bsPrefix="custom-toggle">
            . . .
            </Dropdown.Toggle>

            <Dropdown.Menu>
{
  (feed.userId == userContext.userId) ?
    <>
      <Dropdown.Item onClick={()=>{openEditBox(); handleFeedClick();}}>수정하기</Dropdown.Item>
      <Dropdown.Item onClick={openDeleteBox}>삭제하기</Dropdown.Item>
    </>  : 
    <>
      <Dropdown.Item href="#/action-2">저장하기</Dropdown.Item>
      <Dropdown.Item href="#/action-2">관심없음</Dropdown.Item>
      <Dropdown.Item href="#/action-2">혼인신고하기</Dropdown.Item>
    </>
}
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </Stack>
      { id ? 
            <p className={`px-5 ${styles.content}`} >
              {feed.content}
            </p> 
            :
      <Link className={`px-5`} href={`/post/${feed.feedId}`} onClick={handleFeedClick}>
        <p className={styles.content}>
          {feed.content}
        </p>
      </Link>
    }
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


