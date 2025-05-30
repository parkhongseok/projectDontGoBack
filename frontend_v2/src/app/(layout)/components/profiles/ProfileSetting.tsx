"use client";

import { Dispatch, SetStateAction, useState } from "react";
import "../../globals.css";
import { useUser } from "../../contexts/UserContext";
import styles from "../Feed.module.css";
import { Col, Container, Form, OverlayTrigger, Row, Stack, Tooltip } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLink } from "@fortawesome/free-solid-svg-icons/faLink";
import Link from "next/link";

type propsType = {
  setIsSettingOpen: Dispatch<SetStateAction<boolean>>;
};

export default function ProfileSetting({ setIsSettingOpen }: propsType) {
  const [profileSettingState, setProfileSettingState] = useState(true);
  const { userContext } = useUser();

  const handleToggle1 = () => {
    setProfileSettingState((prev) => !prev);
  };

  const handleAccountEdit = () => {};

  const handlerClose = () => {
    setIsSettingOpen(false);
  };

  const handleSubmit = async () => {
    // 요청 객체
    // const profileSettingRequest = {};
    // const method = "POST";
    // const url = "http://localhost:8090/api/v1/feeds";
    // const body = profileSettingRequest;
    // const success = (result: {}) => {};
    // const fail = () => {
    // alert("서버 오류가 발생했습니다.");
    // };
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
              <h6 className={`ms-auto more ${styles.createBoxTop} ps-3 pb-2`}>. . .</h6>
            </Stack>
            <hr className="feed-underline fontGray4 mt-4" />
            <Container className="d-flex align-items-center">
              <Row className="ms-4 me-4 mt-2 w-100 align-items-center">
                <Col className="">
                  <p className={`${styles.settingName} ms-5`}>연결된 계정</p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <div className={`${styles.settingEmailContainer} d-flex align-items-center p-2`}>
                    <p className={`${styles.settingName} mx-auto`}>{userContext?.email}</p>
                  </div>
                </Col>
              </Row>
            </Container>
            <hr className="feed-underline fontGray4 mt-4" />
            <Container className="d-flex align-items-center">
              <Row className="ms-4 me-4 w-100 align-items-center">
                <Col className="">
                  <p className={`${styles.settingName} ms-5`}>프로필 비공개</p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <div className={` d-flex align-items-center p-2`}>
                    <Form.Check
                      reverse
                      type="switch"
                      id="custom-switch"
                      checked={!profileSettingState}
                      onChange={handleToggle1}
                      className={`${styles.settingSwitch}`}
                    />
                  </div>
                </Col>
              </Row>
            </Container>

            <hr className="feed-underline fontGray4 mt-3" />
            <Container className="d-flex align-items-center">
              <Row className="ms-4 me-4 w-100 align-items-center">
                <Col className="">
                  <p className={`${styles.settingName} ms-5`} onClick={handleAccountEdit}>
                    계정 비활성화 또는 삭제
                  </p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <Link href={"/settings"} className={` d-flex align-items-center p-2`}>
                    <FontAwesomeIcon
                      icon={faLink}
                      className={`fontGray3 cusorPointer`}
                      onClick={handleAccountEdit}
                    />
                  </Link>
                </Col>
              </Row>
            </Container>

            <hr className="feed-underline fontGray4 mt-3" />
            {/* 글쓰기 영역*/}
            <Stack gap={3} className="mx-5">
              <>
                <OverlayTrigger
                  key={"bottom"}
                  placement={"bottom"}
                  overlay={
                    <Tooltip id={`tooltip-${"bottom"}`}>
                      <strong>{"아직"}</strong> 지원하지 않는 기능입니다.
                    </Tooltip>
                  }
                >
                  <button
                    className={`ms-auto mb-1 ${styles.write} custom-button`}
                    onClick={handleSubmit}
                  >
                    저장
                  </button>
                </OverlayTrigger>
              </>
            </Stack>
          </Stack>
        </div>
      </div>
    </div>
  );
}
