'use client'

import "../../globals.css";
import { useEffect, useState } from "react";
import {Stack} from 'react-bootstrap';
import Feed from "../../components/Feed";
import CreateComment from "../../components/CreateComment";
import * as Types from "../../utils/types";
import Dummys from "../../utils/dummyData";
import Comment from '../../components/Comment';
import { useParams } from "next/navigation";
import { useFeed } from "../../contexts/FeedContext"
import { httpRequest } from "../../utils/httpRequest";

export default function FeedDetile() {
  const user = Dummys.User;
  const { feedContext, setFeedContext } = useFeed();
  const { id } = useParams<{ id: string }>();
  const [comments] = useState<Types.Comment[]>(Dummys.Comments);

  useEffect(()=>{
    let isSaved : boolean = false;

    // 만약 컨텍스트에 저장된 피드가 있고, 이게 현재 url의 id와 동일하다면, 이대로 사용
    if (feedContext && `${feedContext.feedId}` === id){ 
      console.log("정상 상황 : 메인 피드 -> 상세 피드")
      isSaved = true;
    }

    // 다르면 우선 로컬 스토리지에서 갖고 오도록 시도 후 다시 비교
    // 로컬 스토리지에 저장이 되어있고, 피드컨텍스트도 존재하지만, id가 다른 경우
    else {
      // 피드 컨텍스트가 없는 경우 (새로고침하면 언제나 피드 컨텍스트가 날아가는군)
      if (!feedContext){
        // console.log(" 새로고침 or url로 이동 ")
        const savedFeed = localStorage.getItem("feedContext");
        // 로컬 스토리지 있다면 불러와서, 비교
        if (savedFeed) {
          // console.log(" 이 전에 이미 상세 피드에 한 번 들어가거나, 수정해서 로컬 스토리지에 게시글 존재 ")
          const myFeed = JSON.parse(savedFeed);
          if(myFeed.feedId == id){
            // console.log(" 상세 페이지에서 단순 새로 고침한 경우 ")
            isSaved = true;
            setFeedContext(myFeed);
          }
        }
      }
      
    }
    
    // 로컬 스토리지가 비어있거나, 컨텍스트가 비어있거나, 혹은 현재 게시물과 id가 다른 url인 경우
    if (!isSaved){
      const method = "GET";
      const url = `http://localhost:8090/api/v1/feeds/${id}`;
      const body = null;
      const success = (result : any) => { 
        if (result){
          setFeedContext(result.data);
          // 저장은 자동
          // localStorage.setItem("feedContext", JSON.stringify(result.data));
        }
        else
          console.error("data is null") // 예외처리
      };
      const fail = () => { console.error("fail") }
      httpRequest(method, url, body, success, fail);
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
                <CreateComment feed={feedContext}/>
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
