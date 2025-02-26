import "../globals.css";

import styles from "./Feed.module.css";
import { Dropdown, Stack } from "react-bootstrap";
import * as Types from "../utils/types";
import Link from "next/link";
import { useFeed } from "../contexts/FeedContext";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import EditPopUp from "./EditPopUp";
import DeletePopUp from "./DeletePopUp";
import { useUser } from "../contexts/UserContext";
import { timeAgo } from "../utils/timeUtils";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart } from "@fortawesome/free-solid-svg-icons";
import { faComment } from "@fortawesome/free-solid-svg-icons";
import { httpRequest } from "../utils/httpRequest";

type FeedProps = {
  feed: Types.Feed | null; // Props의 타입 정의
};

export default function Feed({ feed }: FeedProps) {
  const { userContext } = useUser();
  const { feedContext, updateFeedContext } = useFeed();
  const { pathVariable } = useParams<{ pathVariable: string }>();
  const [isFeedEditOpen, setIsFeedEditOpen] = useState(false);
  const [isFeedDeleteOpen, setIsFeedDeleteOpen] = useState(false);

  const [likeCountState, setFeedLikeState] = useState(feed?.likeCount || 78);
  const [isLikedState, setIsLikedState] = useState(feed?.isLiked);
  const [feedState, setFeedState] = useState(feed);

  useEffect(() => {
    // 디테일 페이지에서 새로고침 시, 로컬 스토리지에서 전달받은 데이터가 state에 할당되기 전에 피드가 업데이트 되는 문제 해결
    if (!feed) return;
    setFeedLikeState(feed.likeCount);
    setIsLikedState(feed.isLiked);
    // console.log("[Feed.tsx] 게시물 정보 로딩 완료 : ", feed);
  }, [feed]);
  // const [feedState, setFeedState] = useState(feed);
  // props로 받은 피드가 변경되더라도 적용가능하게, 전달받은 feed를 props내부에서 사용 -> 좋아요 변경될 때마다 props 변경
  // 본문 눌렀을 때, 전달할 updateContext 내부의 사항은 feedState의 것으로 업데이트
  // 근데 바뀌는 부분이 한정되어있으니까, 이를 한정적으로만 사용해도 괜찮을듯? 본문 같은 건 항상 바뀔 일이 없으니까
  // ㄴㄴ 피드 본문 클릭했을 때, 컨텍스트가 존재하고, props로 전달받은 피드 정보랑 id가 일치하면, 컨텍스트 정보는 갱신된 정보니까
  // 이를 로컬스토리지에 갱신

  if (!feed || !userContext) {
    // if (feed)
    // console.log("[Feed.tsx] 게시물 정보 로딩 완료 : ", feed);
    if (userContext) console.log("[Feed.tsx] 유저 정보 로딩 완료 : ", feed);
    return <div className="loading">피드 또는 유저 로딩중</div>; // feed가 없으면 로딩 중인 상태 표시 공간 컴포넌트로 대체 고민
  }
  const fetchFeedLike = () => {
    const method = "GET";
    const url = `http://localhost:8090/api/v1/feedLikes/${feed.feedId}`;
    const body = null;
    const success = (result: any) => {
      // 실제로 좋아요 요청이 성공한 경우에만 로컬 스토리지에 갱신
      if (feed) {
        result.data.isLiked
          ? updateFeedContext({
              ...feed,
              likeCount: likeCountState + 1,
              isLiked: result.data.isLiked,
            })
          : updateFeedContext({
              ...feed,
              likeCount: likeCountState - 1,
              isLiked: result.data.isLiked,
            });
      } // 이렇게 컨텍스트에 반영해야, 특정 액션 시 로컬스토리지에 저장됨!
    };
    const fail = () => {
      console.log("좋아요 실패");
    };
    httpRequest(method, url, body, success, fail);
  };

  const handleFeedDeleteClick = () => {
    setIsFeedDeleteOpen(true);
  };
  const handleFeedEditClick = () => {
    setIsFeedEditOpen(true);
    updateFeedContext(feed);
  };
  const handleFeedClick = () => {
    feed.feedId == feedContext?.feedId;
    updateFeedContext(feed.feedId == feedContext?.feedId ? feedContext : feed);
  };
  const handleFeedLike = () => {
    setFeedLikeState((prev) => (isLikedState ? prev - 1 : prev + 1));
    setIsLikedState(!isLikedState);

    fetchFeedLike();
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
      {pathVariable ? (
        <p className={`px-5 ${styles.content}`}>{feed.content}</p>
      ) : (
        <Link className={`px-5 `} href={`/post/${feed.feedId}`} legacyBehavior>
          <p onClick={handleFeedClick} className={`${styles.content} pointer`}>
            {feed.content}
          </p>
        </Link>
      )}
      <Stack className="px-5" direction="horizontal" gap={3}>
        <div className={`flex items-center gap-2 text-xl leading-none`}>
          <FontAwesomeIcon
            icon={faHeart}
            className={`${styles.like} ${styles.likeBtn} ${isLikedState && styles.likeActive} `}
            onClick={handleFeedLike}
          />
          <span className={`${styles.like} ms-1`}>{likeCountState}</span>
        </div>

        <div className="vr" />

        <div className={`flex items-center gap-2 text-xl leading-none`}>
          {pathVariable ? (
            <>
              <FontAwesomeIcon
                icon={faComment}
                // size="2x"
                className={` ${styles.comment} ${styles.likeBtn} `}
              />
              <span className={`${styles.comment} ms-1`}>{feed.commentCount}</span>
            </>
          ) : (
            <>
              <FontAwesomeIcon
                icon={faComment}
                // size="2x"
                className={` ${styles.comment} ${styles.likeBtn} pointer`}
              />
              <Link href={`/post/${feed.feedId}`} legacyBehavior>
                <span className={`${styles.comment} pointer ms-1`}>{feed.commentCount}</span>
              </Link>
            </>
          )}
        </div>
      </Stack>
    </Stack>
  );
}
