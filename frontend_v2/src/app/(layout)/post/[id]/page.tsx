"use client";

import "../../globals.css";
import { useEffect, useState } from "react";
import { Stack } from "react-bootstrap";
import Feed from "../../components/Feed";
import CreateComment from "../../components/comments/CreateComment";
import Comment from "../../components/comments/Comment";
import * as Types from "../../utils/types";
import Dummys from "../../utils/dummyData";
import { useParams, useRouter } from "next/navigation";
import { useFeed } from "../../contexts/FeedContext";
import { httpRequest } from "../../utils/httpRequest";
import { useUser } from "../../contexts/UserContext";

export default function FeedDetile() {
  const { feedContext, updateFeedContext, commentContext, crudMyComment, setCrudMyComment } =
    useFeed();
  const { userContext, fetchUserContext } = useUser();
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const [comments, setComments] = useState<Types.Comment[]>([]);
  const getFeedInLocal = () => {
    if (feedContext?.feedId?.toString() === id) return true;
    const savedFeed = localStorage.getItem("feedContext");
    if (savedFeed) {
      const myFeed: Types.Feed = JSON.parse(savedFeed);
      if (myFeed.feedId.toString() === id) {
        console.log("[success] Get Feed in lacal sotrage ");
        updateFeedContext(myFeed);
        return true;
      }
    }
    return false;
  };

  // 로컬 스토리지가 비어있거나, 컨텍스트가 비어있거나, 혹은 현재 게시물과 id가 다른 url인 경우
  const fetchFeed = (id: string) => {
    const method = "GET";
    const url = `http://localhost:8090/api/v1/feeds/${id}`;
    const body = null;
    const success = (result: any) => {
      if (result.data) {
        console.log(result);
        updateFeedContext(result.data);
      } else {
        // window.location.href = "/";
        router.push("/");
        alert("존재하지 않는 게시물입니다");
      }
    };
    const fail = () => {
      console.error(`${id}번 게시물 조회 실패`);
    };
    if (!getFeedInLocal()) {
      httpRequest(method, url, body, success, fail);
    }
  };

  const fetchComments = (id: string) => {
    const method = "GET";
    const url = `http://localhost:8090/api/v1/comments/${id}`;
    const body = null;
    const success = (result: any) => {
      // console.log(result);
      setComments(result.data.comments);
    };
    const fail = () => {
      console.error(`${id}번 게시물의 댓글 조회 실패`);
    };
    httpRequest(method, url, body, success, fail);
  };

  const fetchUserAndFeed = async (id: string) => {
    try {
      if (!userContext?.userId) await fetchUserContext();
      await Promise.all([fetchFeed(id), fetchComments(id)]);
    } catch (error) {
      console.error("데이터 불러오기 중 오류 발생", error);
    }
  };

  useEffect(() => {
    fetchUserAndFeed(id);
  }, []);

  // 답글 부분 comment
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
  };
  // 나의 피스 생성 반영 함수
  const createMyComment = (createdComment: Types.Comment) => {
    setComments((prevComments) => [createdComment, ...prevComments]);
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
      <p className="text-center mb-4 pt-4">Post</p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`pt-4 feed-detail-container`}>
        {/* 본격 사용 가능 공간 */}
        <Stack gap={3} direction="vertical" className="pb-5 pt-2">
          {/* 본문 */}
          <Feed feed={feedContext} />
          {/* 댓글 경계 */}
          <Stack gap={2}>
            <hr className="init fontGray1" />
            <h5 className=" px-5 fontWhite">답글</h5>
            <hr className="init" />
          </Stack>
          {/* 댓글 */}
          {/* 댓글 생성 쓰기 */}
          <div className="my-3">
            <CreateComment feed={feedContext} />
          </div>

          {/* 댓글 공간 */}
          {comments.map((item, idx) => (
            <div key={idx}>
              <Comment comment={item} />
              <hr className="init mt-3 fontGray1" />
            </div>
          ))}
        </Stack>
      </div>
    </>
  );
}
