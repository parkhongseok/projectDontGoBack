"use client";

import "../../globals.css";
import { useEffect, useState } from "react";
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
    updateFeedContext,
    commentContext,
    crudMyComment,
    setCrudMyComment,
  } = useFeed();

  const { userContext, fetchUserContext } = useUser();
  const { feedId } = useParams<{ feedId: string }>();
  const router = useRouter();
  const [comments, setComments] = useState<Types.Comment[]>([]);
  const getFeedInLocal = () => {
    if (feedContext?.feedId?.toString() === feedId) return true;
    const savedFeed = localStorage.getItem("feedContext");
    if (savedFeed) {
      const lacalStorageFeed: Types.Feed = JSON.parse(savedFeed);
      if (lacalStorageFeed.feedId.toString() === feedId) {
        setFeedContext(lacalStorageFeed);
        console.log("[post/id/page] Success to Get Feed in lacal sotrage ");
        return true;
      }
    }
    return false;
  };

  // 로컬 스토리지가 비어있거나, 컨텍스트가 비어있거나, 혹은 현재 게시물과 id가 다른 url인 경우
  const fetchFeed = async (feedId: string) => {
    if (getFeedInLocal()) return;

    const method = "GET";
    const url = `http://localhost:8090/api/v1/feeds/${feedId}`;
    const body = null;

    return new Promise<void>((resolve, reject) => {
      const success = (result: any) => {
        if (result.data) {
          console.log("[post/id/page] 게시물 조회 성공", result);
          updateFeedContext(result.data);
          resolve();
        } else {
          router.push("/");
          alert("존재하지 않는 게시물입니다");
          reject();
        }
      };

      const fail = () => {
        console.error(`${feedId}번 게시물 조회 실패`);
        reject();
      };

      httpRequest(method, url, body, success, fail);
    });
  };

  const fetchComments = (feedId: string) => {
    const method = "GET";
    const url = `http://localhost:8090/api/v1/comments/${feedId}`;
    const body = null;
    const success = (result: any) => {
      // console.log(result);
      setComments(result.data.comments);
    };
    const fail = () => {
      console.error(`${feedId}번 게시물의 댓글 조회 실패`);
    };
    httpRequest(method, url, body, success, fail);
  };

  const fetchUserAndFeed = async (feedId: string) => {
    try {
      if (!userContext?.userId) await fetchUserContext();
      await fetchFeed(feedId); // 데이터가 다 불러와질 때까지 기다리기
      fetchComments(feedId);
    } catch (error) {
      console.error("데이터 불러오기 중 오류 발생", error);
    }
  };

  useEffect(() => {
    fetchUserAndFeed(feedId);
  }, []);

  // 답글 부분 comment
  // 나의 피드 생성 반영 함수
  const createMyComment = (createdComment: Types.Comment) => {
    setComments((prevComments) => [createdComment, ...prevComments]);
    if (feedContext) {
      // 댓글 개수 프론트에서도 즉시 반영
      updateFeedContext({ ...feedContext, commentCount: feedContext?.commentCount + 1 });
    }
  };
  // 나의 피드 수정 반영 함수
  const updateMyComment = (updatedComment: Types.Comment) => {
    setComments((prevComments) =>
      prevComments.map((comment) =>
        comment.commentId === updatedComment.commentId ? updatedComment : comment
      )
    );
  };
  // 나의 피드 삭제 반영 함수
  const deleteMyComment = (deletedComment: Types.Comment) => {
    setComments((prevComments) =>
      prevComments.filter((comment) => comment.commentId !== deletedComment.commentId)
    );
    if (feedContext) {
      // 댓글 개수 프론트에서도 즉시 반영
      updateFeedContext({ ...feedContext, commentCount: feedContext?.commentCount - 1 });
    }
  };

  // 자신 답글의 수정 삭제를 감지하여, 이를 반영
  useEffect(() => {
    // 생성 작업
    if (crudMyComment.C) {
      setCrudMyComment({ ...crudMyComment, C: false });
      if (commentContext) createMyComment(commentContext);
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
      console.log(`[f${commentContext?.feedId}-c${commentContext?.commentId}] 답글 삭제 요청 감지`);
    }
  }, [crudMyComment]);

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
