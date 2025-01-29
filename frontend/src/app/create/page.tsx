'use client'

import CreateBox from "../components/CreateBox";
import "../globals.css";

type TypeOfCreateFeed = {
  userId: number;
  userName: string;
  feedType: string;
};

type userProps = {
  user : TypeOfCreateFeed; // Props의 타입 정의
};
export default function Create(){

  return (
    <CreateBox user={{
      userId: 0,
      userName: "",
      feedType: ""
    }}></CreateBox>
  )
}


