'use client'

import "../../globals.css";
import { useEffect, useState } from "react";
import {Stack} from 'react-bootstrap';
import Feed from "../../components/Feed";
import CreateComment from "../../components/CreateComment";
import * as Types from "../../types";
import Dummys from "../../dummyData";
import Comment from '../../components/Comment';
import { useParams } from "next/navigation";
import { useFeed } from "../../context/FeedContest"

export default function FeedDetile() {
  const user = Dummys.User;
  const { feedContext, setFeedContext } = useFeed();
  const { id } = useParams<{ id: string }>();
  const [comments] = useState<Types.Comment[]>(Dummys.Comments);

  useEffect(()=>{
    if (!feedContext){
      const savedFeed = localStorage.getItem("feedContext");
      if (savedFeed) {
        setFeedContext(JSON.parse(savedFeed));
      } else {
        fetch(`http://localhost:8090/api/v1/feeds/${id}`)
        .then((res) => res.json())
        .then((data) => {
          setFeedContext(data)
          localStorage.setItem("feedContext", JSON.stringify(data));
          });
        }
      }
    }, [id]);

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <p className="text-center mb-4 pt-4">Post</p>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
        {/* 사이드바가 차지하지 않는 나머지 공간 */}
        <div className={`pt-4 feed-detail-container`}>
          {/* 본격 사용 가능 공간 */}
          <Stack gap={3} direction="vertical" className="pb-5 pt-2" >
            {/* 본문 */}
            <Feed feed = {feedContext}/>
            {/* 댓글 경계 */}
            <Stack gap={2} >
              <hr className="init fontGray1"/>
              <h5 className=" px-5 fontWhite">답글</h5>
              <hr className="init"/>
            </Stack>
            {/* 댓글 */}
              {/* 댓글 생성 쓰기 */}
              <div className="my-3">
                <CreateComment feed = {feedContext} user={user}/>
              </div>

              {/* 댓글 공간 */}
              {
                comments.map((item, idx)  =>
                  <div key={idx}>
                    <Comment comment = {item}/>
                    <hr className="init mt-3 fontGray1"/>
                  </div>
                )
              }
          </Stack>
        </div>

    </>
  );
}
