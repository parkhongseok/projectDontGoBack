import "../globals.css";

import styles from "./Feed.module.css";
import { Dropdown, Spinner, Stack } from "react-bootstrap";
import * as Types from "../utils/types";
import Link from "next/link";
import { useFeed } from "../contexts/FeedContext";
import { useParams, usePathname } from "next/navigation";
import { useEffect, useState } from "react";
import EditPopUp from "./EditPopUp";
import DeletePopUp from "./DeletePopUp";
import { useUser } from "../contexts/UserContext";
import { timeAgo } from "../utils/timeUtils";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart } from "@fortawesome/free-solid-svg-icons";
import { faComment } from "@fortawesome/free-solid-svg-icons";
import { httpRequest } from "../utils/httpRequest";
import { BACKEND_API_URL } from "../utils/globalValues";
import Badge from "./Badge";

type PropsType = {
  feed: Types.Feed | null; // Props의 타입 정의
};

export default function Feed({ feed }: PropsType) {
  const pathname = usePathname();
  const { feedId } = useParams<{ feedId: string }>();
  const { feedContext, setFeedContext, setCrudMyFeed } = useFeed();
  const { userContext } = useUser();

  const [isFeedEditOpen, setIsFeedEditOpen] = useState(false);
  const [isFeedDeleteOpen, setIsFeedDeleteOpen] = useState(false);

  const [feedState, setFeedState] = useState(feed);

  useEffect(() => {
    if (!feed) return;
    setFeedState(feed);
  }, [feed]);

  if (!feed || !userContext) {
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ minHeight: "200px" }}>
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading...</span>
        </Spinner>
      </div>
    );
  }

  const fetchFeedLike = () => {
    const method = "GET";
    const url = `${BACKEND_API_URL}/v1/feedLikes/${feed.feedId}`;

    const body = null;
    const success = (result: Types.ResData<{ likeCount: number; isLiked: boolean }>) => {
      if (feedState) {
        setFeedContext({
          ...feedState,
          likeCount: result.data.likeCount,
          isLiked: result.data.isLiked,
        });
      }
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
    setFeedContext(feed.feedId == feedContext?.feedId ? feedContext : feed);
  };
  const handleFeedClick = () => {
    if (feed.feedId == feedContext?.feedId)
      setFeedContext(feed.feedId == feedContext?.feedId ? feedContext : feed);
  };
  const handleFeedLike = () => {
    if (feedState) {
      const likedFeed = {
        ...feedState,
        isLiked: !feedState.isLiked,
        likeCount: feedState.isLiked ? feedState.likeCount - 1 : feedState.likeCount + 1,
      };
      setFeedState(likedFeed); // 즉시 화면에 보여지는 용
      setFeedContext(likedFeed); // MainFeed에 정보 갱신용
    }
    fetchFeedLike();
    if (pathname === "/" || /\/profile\/\d+$/.test(pathname))
      setCrudMyFeed({ C: false, R: false, U: true, D: false });
  };


  // 뱃지를 렌더링하는 헬퍼 함수
  const renderBadge = () => {
    // 최우선 순위: 내가 쓴 글인지 확인
    if (feed.userId === userContext.userId) {
      return <Badge role="me">나</Badge>;
    }
    // 관리자가 쓴 글인지 확인
    if (feed.userRole === 'ADMIN') {
      return <Badge role="admin">관리자</Badge>;
    }
    // 방문자가 쓴 글인지 확인
    if (feed.userRole === 'GUEST') {
      return <Badge role="guest">방문자</Badge>;
    }
    // 그 외 일반 유저는 뱃지를 표시하지 않음
    return null;
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
        <div className="d-flex align-items-center"> {/* 이름과 뱃지를 정렬하기 위한 div */}
          <Link className="px-5" href={`/profile/${feed.userId}`} legacyBehavior>
            <p className={`${styles.userName} ${feedTypeClass} cusorPointer mb-0`}>{feed.author}</p>
          </Link>
          {renderBadge()} {/* 헬퍼 함수 호출 */}
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
                  <Dropdown.ItemText className="fontGray1">보관하기</Dropdown.ItemText>
                  <Dropdown.Item onClick={handleFeedEditClick}>수정하기</Dropdown.Item>
                  <Dropdown.Item onClick={handleFeedDeleteClick}>삭제하기</Dropdown.Item>
                </>
              ) : (
                <>
                  <Dropdown.ItemText className="fontGray1">저장하기</Dropdown.ItemText>
                  <Dropdown.ItemText className="fontGray1">관심없음</Dropdown.ItemText>
                  <Dropdown.ItemText className="fontGray1">혼인신고하기</Dropdown.ItemText>
                </>
              )}
            </Dropdown.Menu>
          </Dropdown>
        </div>
      </Stack>
      {feedId ? (
        <div>
          <p className={`px-5 ${styles.content}`}>{feed.content}</p>
        </div>
      ) : (
        <div className={`${styles.contentContainer}`}>
          <Link className={`px-5 `} href={`/post/${feed.feedId}`} legacyBehavior>
            <p onClick={handleFeedClick} className={`${styles.content} cusorPointer`}>
              {feed.content}
            </p>
          </Link>
        </div>
      )}
      <Stack className="px-5" direction="horizontal" gap={3}>
        <div className={`flex items-center gap-2 text-xl leading-none`}>
          <FontAwesomeIcon
            icon={faHeart}
            className={`${styles.like} ${styles.likeBtn} ${
              feedState?.isLiked && styles.likeActive
            } `}
            onClick={handleFeedLike}
          />
          <span className={`${styles.like} ms-1`}>{feedState?.likeCount}</span>
        </div>

        <div className="vr" />

        <div className={`flex items-center gap-2 text-xl leading-none`}>
          {feedId ? (
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
              <Link href={`/post/${feed.feedId}`}>
                <FontAwesomeIcon
                  icon={faComment}
                  // size="2x"
                  className={` ${styles.comment} ${styles.likeBtn} cusorPointer`}
                />
                <span className={`${styles.comment} ms-1 cusorPointer`}>{feed.commentCount}</span>
              </Link>
            </>
          )}
        </div>
      </Stack>
    </Stack>
  );
}
