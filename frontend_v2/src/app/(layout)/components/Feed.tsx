import "../globals.css";

import styles from "./Feed.module.css";
import { Dropdown, Stack } from "react-bootstrap";
import * as Types from "../utils/types";
import Link from "next/link";
import { useFeed } from "../contexts/FeedContext";
import { useParams } from "next/navigation";
import { useState } from "react";
import EditPopUp from "./EditPopUp";
import DeletePopUp from "./DeletePopUp";
import { useUser } from "../contexts/UserContext";
import { timeAgo } from "../utils/timeUtils";

type FeedProps = {
  feed: Types.Feed | null; // Props의 타입 정의
};

export default function Feed({ feed }: FeedProps) {
  const { userContext } = useUser();
  const { updateFeedContext } = useFeed();
  const { id } = useParams<{ id: string }>();
  // 이름좀 바꾸자
  const [isFeedEditOpen, setIsFeedEditOpen] = useState(false);
  const [isFeedDeleteOpen, setIsFeedDeleteOpen] = useState(false);

  if (!feed || !userContext) {
    if (feed) console.log("[Feed.tsx] 게시물 정보 로딩 완료 : ", feed);
    if (userContext) console.log("[Feed.tsx] 유저 정보 로딩 완료 : ", feed);
    return <div className="loading">피드 또는 유저 로딩중</div>; // feed가 없으면 로딩 중인 상태 표시 공간 컴포넌트로 대체 고민
  }
  const handleFeedDeleteClick = () => {
    setIsFeedDeleteOpen(true);
  };
  const handleFeedEditClick = () => {
    setIsFeedEditOpen(true);
    updateFeedContext(feed);
  };
  const handleFeedClick = () => {
    updateFeedContext(feed);
  };
  // post.module.css에서 []로 검색한 class의 주소를 반환, 없다면 ""
  const feedTypeClass = styles[feed.feedType] || "";

  return (
    <Stack className="px-5" gap={3}>
      {isFeedEditOpen && <EditPopUp setIsFeedEditOpen={setIsFeedEditOpen} />}
      {isFeedDeleteOpen && (
        <DeletePopUp feedId={feed.feedId} setIsFeedDeleteOpen={setIsFeedDeleteOpen} />
      )}
      <Stack direction="horizontal" gap={3}>
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>{feed.author}</p>
        </div>
        <div className="vr" />
        <div className="">
          <p className={styles.time}>{timeAgo(feed.createdAt, feed.updatedAt)}</p>
        </div>
        <div className="ms-auto">
          <Dropdown>
            <Dropdown.Toggle
              className={styles.more}
              as="div"
              id="dropdown-basic"
              bsPrefix="custom-toggle"
            >
              . . .
            </Dropdown.Toggle>

            <Dropdown.Menu>
              {feed.userId == userContext.userId ? (
                <>
                  <Dropdown.Item>보관하기</Dropdown.Item>
                  <Dropdown.Item onClick={handleFeedEditClick}>수정하기</Dropdown.Item>
                  <Dropdown.Item onClick={handleFeedDeleteClick}>삭제하기</Dropdown.Item>
                </>
              ) : (
                <>
                  <Dropdown.Item href="#/action-2">저장하기</Dropdown.Item>
                  <Dropdown.Item href="#/action-2">관심없음</Dropdown.Item>
                  <Dropdown.Item href="#/action-2">혼인신고하기</Dropdown.Item>
                </>
              )}
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </Stack>
      {id ? (
        <p className={`px-5 ${styles.content}`}>{feed.content}</p>
      ) : (
        <Link
          className={`px-5 `}
          href={`/post/${feed.feedId}`}
          onClick={handleFeedClick}
          legacyBehavior
        >
          <p className={styles.content}>{feed.content}</p>
        </Link>
      )}
      <Stack className="px-5" direction="horizontal" gap={3}>
        <div className="">
          <p className={styles.like}>좋아요 {feed.likeCount}개</p>
        </div>
        <div className="vr" />
        <div className="">
          <p className={styles.comment}>댓글 {feed.commentCount}개</p>
        </div>
      </Stack>
    </Stack>
  );
}
