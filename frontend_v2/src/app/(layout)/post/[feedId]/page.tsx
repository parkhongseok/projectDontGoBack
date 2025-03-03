"use client";

import "../../globals.css";
import { useEffect, useRef, useState } from "react";
import { Stack } from "react-bootstrap";
import Feed from "../../components/Feed";
import CreateComment from "../../components/comments/CreateComment";
import Comment from "../../components/comments/Comment";
import * as Types from "../../utils/types";
import { useParams, useRouter } from "next/navigation";
import { useFeed } from "../../contexts/FeedContext";
import { httpRequest } from "../../utils/httpRequest";
import { useUser } from "../../contexts/UserContext";

export default function FeedDetile() {
  const {
    feedContext,
    setFeedContext,
    commentContext,
    setCommentContext,
    crudMyComment,
    setCrudMyComment,
  } = useFeed();
  const { userContext, fetchUserContext } = useUser();
  const { feedId } = useParams<{ feedId: string }>();
  // const router = useRouter();
  const [comments, setComments] = useState<Types.Comment[]>([]);
  // 댓글 패이징
  const [lastCommentId, setLastCommentId] = useState(0);
  const [commentsLoading, setCommentsLoading] = useState(false);
  const lastCommentIdRef = useRef(lastCommentId);
  useEffect(() => {
    //클로저
    lastCommentIdRef.current = lastCommentId;
  }, [lastCommentId]);

  const fetchFeed = async () => {
    const method = "GET";
    const url = `http://localhost:8090/api/v1/feeds/${feedId}`;
    const body = null;
    const success = (result: any) => {
      if (result.data) {
        setFeedContext(result.data);
      } else {
        alert("존재하지 않는 게시물입니다");
      }
    };
    const fail = () => {
      console.error(`${feedId}번 게시물 조회 실패`);
    };
    httpRequest(method, url, body, success, fail);
  };

  const fetchComments = async () => {
    if (commentsLoading) return;
    setCommentsLoading(true); // 로딩 시작

    const method = "GET";
    const url = `http://localhost:8090/api/v1/comments/${feedId}?lastCommentId=${
      lastCommentIdRef.current
    }&size=${5}`;
    const body = null;
    const success = async (result: any) => {
      setCommentsLoading(false); // 로딩 끝
      let newComments = result.data.comments;
      if (newComments.length == 0) return;
      setComments((prevComments: Types.Comment[]) => [...prevComments, ...newComments]);
      setLastCommentId(newComments[newComments.length - 1].commentId);
    };
    const fail = () => {
      setCommentsLoading(false); // 로딩 끝
      console.error(`${feedId}번 게시물의 댓글 조회 실패`);
    };
    httpRequest(method, url, body, success, fail);
  };

  // 상세 피드에 필요한 데이터만 fetch
  useEffect(() => {
    const fetchInitialData = async () => {
      if (!userContext?.userId) await fetchUserContext();
      if (!feedContext?.feedId) await fetchFeed();
      if (lastCommentId == 0) await fetchComments();
    };
    fetchInitialData();
  }, []);

  // 답글 부분 comment
  // 나의 답글 생성 반영 함수
  const createMyComment = (createdComment: Types.Comment) => {
    setComments((prevComments) => [createdComment, ...prevComments]);
    // if (commentContext) {
    //   // 댓글 개수 프론트에서도 즉시 반영
    //   setCommentContext({
    //     ...commentContext,
    //     subCommentCount: commentContext?.subCommentCount + 1,
    //   });
    // }
  };
  // 나의 답글 수정 반영 함수
  const updateMyComment = (updatedComment: Types.Comment) => {
    setComments((prevComments) =>
      prevComments.map((comment) =>
        comment.commentId === updatedComment.commentId ? updatedComment : comment
      )
    );
  };
  // 나의 답글 삭제 반영 함수
  const deleteMyComment = (deletedComment: Types.Comment) => {
    setComments((prevComments) =>
      prevComments.filter((comment) => comment.commentId !== deletedComment.commentId)
    );
    if (commentContext) {
      // 댓글 개수 프론트에서도 즉시 반영
      setCommentContext({
        ...commentContext,
        subCommentCount: commentContext?.subCommentCount - 1,
      });
    }
  };

  // 자신 답글의 수정 삭제를 감지하여, 이를 반영
  useEffect(() => {
    // 생성 작업
    if (crudMyComment.C) {
      setCrudMyComment({ ...crudMyComment, C: false });
      if (commentContext) {
        createMyComment(commentContext);
        if (comments.length == 0) setLastCommentId(commentContext.commentId); // 이부분이 없으면 첫번째 댓글 조회 시, 동일 내용 추가
      }
      if (feedContext)
        setFeedContext({ ...feedContext, commentCount: feedContext.commentCount + 1 });
      console.log(`[f${commentContext?.feedId}-c${commentContext?.commentId}] 답글 생성 요청 감지`);
    }
    // 수정 작업
    if (crudMyComment.U) {
      setCrudMyComment({ ...crudMyComment, U: false });
      if (commentContext) updateMyComment(commentContext);
      console.log(`[f${commentContext?.feedId}-c${commentContext?.commentId}] 답글 수정 요청 감지`);
    }
    // 삭제 작업
    if (crudMyComment.D) {
      setCrudMyComment({ ...crudMyComment, D: false });
      if (commentContext) deleteMyComment(commentContext);
      if (feedContext)
        setFeedContext({ ...feedContext, commentCount: feedContext.commentCount - 1 });
      console.log(`[f${commentContext?.feedId}-c${commentContext?.commentId}] 답글 삭제 요청 감지`);
    }
  }, [crudMyComment]);

  // [댓글 패이징] 스크롤을 감지하여 마지막에 다다르면 피드를 불러옴
  useEffect(() => {
    let timeoutId: NodeJS.Timeout;

    const handleScroll = () => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        const { scrollTop, scrollHeight, clientHeight } = document.documentElement;

        if (scrollTop + clientHeight >= scrollHeight - 100 && !commentsLoading) {
          fetchComments();
        }
      }, 200); // 200ms 디바운스
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
      clearTimeout(timeoutId);
    };
  }, [commentsLoading, lastCommentId]);

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">POST</h5>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`pt-4 feed-detail-container`}>
        {/* 본격 사용 가능 공간 */}
        <Stack gap={3} direction="vertical" className="pb-5 pt-2">
          {/* 본문 */}
          <Feed feed={feedContext} />
          {/* 댓글 경계 */}
          <div className="feedDetileMiddleLine mt-2">
            {/* <h5 className=" px-5 fontGray3">답글</h5> */}
          </div>
          {/* 댓글 */}
          {/* 댓글 생성 쓰기 */}
          <div className="my-3">
            <CreateComment feed={feedContext} />
          </div>

          {/* 댓글 공간 */}
          {comments.map((item, idx) => (
            <div key={idx}>
              <hr className="init mb-3 feedUnderLine" />
              <Comment comment={item} />
            </div>
          ))}
        </Stack>
      </div>
    </>
  );
}
