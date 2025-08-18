"use client";

import { Dispatch, SetStateAction, useState } from "react";
import "../../globals.css";
import { useUser } from "../../contexts/UserContext";
import styles from "../feeds/Feed.module.css";
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

  const handlerClose = () => {
    setIsSettingOpen(false);
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
            <div className="d-flex justify-content-between align-items-center mx-5">
              {/* 왼쪽: 취소 버튼 */}
              <button
                className={`${styles.write} ${styles.exitBtn} custom-button`}
                onClick={handlerClose}
              >
                취소
              </button>

              {/* 중앙: 제목 */}
              <h6 className={`${styles.createBoxTop} m-0`}>프로필 설정</h6>

              {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
              <button
                className={`${styles.write} ${styles.exitBtn} custom-button`}
                style={{ visibility: "hidden" }}
                aria-hidden="true"
              >
                취소
              </button>
            </div>
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
                      disabled
                    />
                  </div>
                </Col>
              </Row>
            </Container>

            <hr className="feed-underline fontGray4 mt-3" />
            <Container className="d-flex align-items-center">
              <Row className="ms-4 me-4 w-100 align-items-center">
                <Col className="">
                  <p className={`${styles.settingName} ms-5`}>계정 비활성화 또는 삭제</p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <Link href={"/settings"} className={` d-flex align-items-center p-2`}>
                    <FontAwesomeIcon icon={faLink} className={`fontGray3 cusorPointer`} />
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
                      <strong>{"프로필 비공개"}</strong> 기능은 곧 지원 예정입니다.
                    </Tooltip>
                  }
                >
                  <button
                    className={`ms-auto mb-1 ${styles.write} custom-button`}
                    onClick={handlerClose}
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
