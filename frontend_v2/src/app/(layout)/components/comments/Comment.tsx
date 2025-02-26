import "../../globals.css";
import styles from "./../Feed.module.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Dropdown, Stack } from "react-bootstrap";
import * as Types from "../../utils/types";
import { useUser } from "../../contexts/UserContext";
import { useState } from "react";
import { timeAgo } from "../../utils/timeUtils";
import DeleteCommentPopUp from "./DeleteCommentPopUp";
import EditCommentPopUp from "./EditCommentPopUp";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart } from "@fortawesome/free-solid-svg-icons/faHeart";
import { faComment } from "@fortawesome/free-solid-svg-icons/faComment";

type CommentProps = {
  comment: Types.Comment;
};

export default function Comment({ comment }: CommentProps) {
  const { userContext } = useUser();
  if (!userContext) return <div>유저 로딩 중</div>;
  const [isCommentEditOpen, setIsCommentEditOpen] = useState(false);
  const [isCommentDeleteOpen, setIsCommentDeleteOpen] = useState(false);
  const [commentLikeState, setCommentLikeState] = useState(comment.likeCount || 0);
  const [isActiveCommentLike, setIsActiveCommentLike] = useState(false);
  const handleCommentEditClick = () => {
    setIsCommentEditOpen(true);
    // updateFeedContext(feed);
  };
  const handleCommentDeleteClick = () => {
    setIsCommentDeleteOpen(true);
  };
  const handleCommentClick = () => {};
  const handleCommentLike = () => {
    if (isActiveCommentLike) {
      setCommentLikeState((prev) => prev - 1);
      setIsActiveCommentLike(false);
      // 요청 보내기
    } else {
      setCommentLikeState((prev) => prev + 1);
      setIsActiveCommentLike(true);
    }
  };
  const feedTypeClass = styles[comment.commentType] || "";
  return (
    <Stack className="px-5" gap={3}>
      {isCommentEditOpen && <EditCommentPopUp setIsCommentEditOpen={setIsCommentEditOpen} />}
      {isCommentDeleteOpen && (
        <DeleteCommentPopUp
          commentId={comment.commentId}
          setIsCommentDeleteOpen={setIsCommentDeleteOpen}
        />
      )}
      <Stack direction="horizontal" gap={3}>
        <div>
          <p className={`${styles.userName} ${feedTypeClass}`}>{comment.author}</p>
        </div>
        <div className="vr" />
        <div className="">
          <p className={styles.time}>{timeAgo(comment.createdAt, comment.updatedAt)}</p>
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
              {comment.userId == userContext.userId ? (
                <>
                  <Dropdown.Item onClick={handleCommentEditClick}>수정하기</Dropdown.Item>
                  <Dropdown.Item onClick={handleCommentDeleteClick}>삭제하기</Dropdown.Item>
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
      <div className="px-5">
        <p className={styles.content}>{comment.content}</p>
      </div>
      <Stack className="px-5" direction="horizontal" gap={3}>
        {/* <div className="">
          <p className={styles.like}>좋아요 {comment.likeCount}개</p>
        </div>
        <div className="vr" />
        <Link href={`#/feed/feedId/${comment.commentId}`} legacyBehavior>
          <p onClick={handleCommentClick} className={styles.comment}>
            댓글 {comment.subCommentCount}개
          </p>
        </Link> */}
        <div className={`flex items-center gap-2 text-xl leading-none`}>
          <FontAwesomeIcon
            icon={faHeart}
            className={`${styles.like} ${styles.likeBtn} ${
              isActiveCommentLike && styles.likeActive
            } `}
            onClick={handleCommentLike}
          />
          <span className={`${styles.like} ms-1`}>{commentLikeState}</span>
        </div>

        <div className="vr" />

        <div className={`flex items-center gap-2 text-xl leading-none`}>
          <FontAwesomeIcon
            icon={faComment}
            // size="2x"
            className={` ${styles.comment} ${styles.likeBtn} `}
          />
          <Link href={`#/post/${comment.feedId}/${comment.commentId}`} legacyBehavior>
            <span className={`${styles.comment} ms-1`}>{comment.subCommentCount}</span>
          </Link>
        </div>
      </Stack>
    </Stack>
  );
}
