"use client";

import { Dispatch, SetStateAction, useState } from "react";
import "../../globals.css";
import { useUser } from "../../contexts/UserContext";
import { httpRequest } from "../../utils/httpRequest";
import styles from "../Feed.module.css";
import { Form, Stack } from "react-bootstrap";

type propsType = {
  setIsSettingOpen: Dispatch<SetStateAction<boolean>>;
};

export default function ProfileSetting({ setIsSettingOpen }: propsType) {
  const [profileSettingState, setProfileSettingState] = useState(true);
  const [accountStatus, setAccountStatus] = useState(true);
  const { userContext } = useUser();
  const handleToggle1 = () => {
    setProfileSettingState((prev) => !prev);
  };
  const handleToggle2 = () => {
    setAccountStatus((prev) => !prev);
  };

  const handlerClose = () => {
    setIsSettingOpen(false);
  };

  const handleSubmit = async () => {
    // 요청 객체
    const profileSettingRequest = {};
    const method = "POST";
    const url = "http://localhost:8090/api/v1/feeds";
    const body = profileSettingRequest;
    const success = (result: {}) => {};
    const fail = () => {
      alert("서버 오류가 발생했습니다.");
    };

    // httpRequest(method, url, body, success, fail);
  };

  return (
    <div className={`${styles.createBoxlayout} ${styles.overay} ${styles.createBoxBackground}`}>
      <div className="sidebar-space" />
      <div className="main-space">
        {/* dropdown 버튼이 들어올 자리 */}
        <p className="text-center mb-4 pt-3"></p>
        <div className={`pt-4 ${styles.createBoxContainer}`}>
          {/* 사이드바가 차지하지 않는 나머지 공간 */}

          {/* 본격 사용 가능 공간 */}
          <Stack gap={1} direction="vertical" className="pb-4 pt-2">
            {/* 상단  취소 / 게시글 작성 / ... */}
            <Stack direction="horizontal" className="mx-5">
              <>
                <button
                  className={`${styles.write} ${styles.exitBtn} custom-button`}
                  onClick={handlerClose}
                >
                  취소
                </button>
              </>
              <h6 className={`ms-auto ${styles.createBoxTop}`}>프로필 설정</h6>
              <h6 className={`ms-auto more ${styles.createBoxTop} pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontGray4 mt-4" />
            <div className="mt-2 mx-auto">
              <Form className="d-flex align-items-center">
                <p className={`${styles.settingName}`}>프로필 비공개</p>
                <Form.Check
                  reverse
                  type="switch"
                  id="custom-switch"
                  checked={!profileSettingState}
                  onChange={handleToggle1}
                  className={`${styles.settingSwitch} `}
                />
              </Form>
            </div>
            <hr className="feed-underline fontGray4 mt-3" />
            <div className="mt-2 mx-auto">
              <Form className="d-flex align-items-center">
                <p className={`${styles.settingName}`}>계정 비활성화</p>
                <Form.Check
                  reverse
                  type="switch"
                  id="custom-switch"
                  checked={!accountStatus}
                  onChange={handleToggle2}
                  className={`${styles.settingSwitch} `}
                />
              </Form>
            </div>
            <hr className="feed-underline fontGray4 mt-3" />
            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <>
                <button
                  className={`ms-auto mb-1 ${styles.write} custom-button`}
                  onClick={handleSubmit}
                >
                  저장
                </button>
              </>
            </Stack>
          </Stack>
        </div>
      </div>
    </div>
  );
}
