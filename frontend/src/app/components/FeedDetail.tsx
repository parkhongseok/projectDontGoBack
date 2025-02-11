'use client'

import { useState } from "react";
import "../globals.css";
import styles from "./Feed.module.css"
import {Stack} from 'react-bootstrap';
import Feed from "./Feed";
import CreateComment from "./CreateComment";
import * as Types from "../types";
import Dummys from "../dummyData";
import Comment from '../components/Comment';

export default function FeedDetail(){
  const [comments] = useState<Types.Comment[]>(Dummys.Comments);
  const user = Dummys.User;
  const feed = Dummys.Feed;

  return (
  <>
        {/* 사이드바가 차지하지 않는 나머지 공간 */}
        <div className={`pt-4 ${styles.FeedDetailContainer}`}>
          {/* 본격 사용 가능 공간 */}
          <Stack gap={3} direction="vertical" className="pb-5 pt-2" >
            {/* 본문 */}
            <Feed feed = {feed}/>
            {/* 댓글 경계 */}
            <Stack gap={2} >
              <hr className="init fontGray1"/>
              <h5 className=" px-5 fontWhite">답글</h5>
              <hr className="init"/>
            </Stack>
            {/* 댓글 */}
              {/* 댓글 생성 쓰기 */}
              <div className="my-3">
                <CreateComment feed={feed} user={user}/>
              </div>

              {/* 댓글 공간 */}
              {
                comments.map((item, idx) => 
                  <div key={idx}>
                    <Comment comment = {item}/>
                    <hr className="init mt-3 fontGray1"/>
                  </div>
                )
              }
          </Stack>
        </div>
    </>
  )
}


