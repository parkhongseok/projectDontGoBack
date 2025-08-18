import "../../globals.css";
import styles from "../feeds/Feed.module.css";
import "bootstrap/dist/css/bootstrap.min.css";
import { Dropdown, Stack } from "react-bootstrap";
import * as Types from "../../utils/types";
import { useUser } from "../../contexts/UserContext";
import { useEffect, useState } from "react";
import { timeAgo } from "../../utils/timeUtils";
import DeleteCommentPopUp from "./DeleteCommentPopUp";
import EditCommentPopUp from "./EditCommentPopUp";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHeart } from "@fortawesome/free-solid-svg-icons/faHeart";
import { faComment } from "@fortawesome/free-solid-svg-icons/faComment";
import { httpRequest } from "../../utils/httpRequest";
import { useFeed } from "../../contexts/FeedContext";
import { usePathname } from "next/navigation";
import { BACKEND_API_URL } from "../../utils/globalValues";
import Badge from "../badge/Badge";
import BadgeMe from "../badge/BadgeMe";

type CommentProps = {
  comment: Types.Comment;
};

export default function Comment({ comment }: CommentProps) {
  const pathname = usePathname();

  const { userContext } = useUser();
  const { setCommentContext, setCrudMyComment } = useFeed();

  const [isCommentEditOpen, setIsCommentEditOpen] = useState(false);
  const [isCommentDeleteOpen, setIsCommentDeleteOpen] = useState(false);

  // 댓글 좋아요
  // const [likeCountState, setFeedLikeState] = useState(comment?.likeCount || 0);
  // const [isLikedState, setIsLikedState] = useState(comment?.isLiked);
  const [commentState, setCommentState] = useState(comment);

  useEffect(() => {
    // 이부분이 없으니까 , props해주는 값이 변해도, 영향이 없는지, 댓글 좋아요 후 생성하면 한칸씩 밀림
    if (!comment) return;
    setCommentState(comment);
  }, [comment]);

  if (!userContext) return <div>유저 로딩 중</div>;

  const fetchCommentLike = () => {
    const method = "GET";
    const url = `${BACKEND_API_URL}/v1/commentLikes/${comment.commentId}`;
    const body = null;
    const success = (result: Types.ResData<Types.Comment>) => {
      if (commentState) {
        setCommentContext({
          ...commentState,
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

  const handleCommentEditClick = () => {
    setIsCommentEditOpen(true);
    setCommentContext(comment);
  };
  const handleCommentDeleteClick = () => {
    setIsCommentDeleteOpen(true);
  };
  // const handleCommentClick = () => {};
  const handleCommentLike = () => {
    if (commentState) {
      const likedComment = {
        ...commentState,
        isLiked: !commentState.isLiked,
        likeCount: commentState.isLiked ? commentState.likeCount - 1 : commentState.likeCount + 1,
      };
      setCommentState(likedComment); // 즉시반영 (무결성x)
      setCommentContext(likedComment); // 댓글의 메인화면(디테일 게시글 화면에) 즉시반영 (무결성x)
    }
    fetchCommentLike();
    if (/\/post\/\d+$/.test(pathname)) setCrudMyComment({ C: false, R: false, U: true, D: false });
  };

  // 뱃지를 렌더링하는 헬퍼 함수
  const renderBadge = () => {
    return (
      <>
        {/* 역할(Role) 기반 뱃지 */}
        {comment.userRole === "ADMIN" && <Badge role="admin">관리자</Badge>}
        {comment.userRole === "GUEST" && <Badge role="guest">방문자</Badge>}

        {/* '나' 뱃지 (역할과 별개로 항상 표시) */}
        {comment.userId === userContext.userId && <BadgeMe role="me">나</BadgeMe>}
      </>
    );
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
        <div className="d-flex align-items-center">
          {" "}
          {/* 이름과 뱃지를 정렬하기 위한 div */}
          <p className={`${styles.userName} ${feedTypeClass} mb-0`}>{comment.author}</p>
          {renderBadge()} {/* 헬퍼 함수 호출 */}
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
                  <Dropdown.ItemText className="fontGray1">저장하기</Dropdown.ItemText>
                  <Dropdown.ItemText className="fontGray1">관심없음</Dropdown.ItemText>
                  <Dropdown.ItemText className="fontGray1">혼인신고하기</Dropdown.ItemText>
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
        <div className={`flex items-center gap-2 text-xl leading-none`}>
          <FontAwesomeIcon
            icon={faHeart}
            className={`${styles.like} ${styles.likeBtn} ${
              commentState.isLiked && styles.likeActive
            } `}
            onClick={handleCommentLike}
          />
          <span className={`${styles.like} ms-1`}>{commentState.likeCount}</span>
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
