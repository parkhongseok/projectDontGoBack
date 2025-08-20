"use client";

import "../globals.css";
import styles from "../components/feeds/Feed.module.css";
import { Col, Container, Form, Row, Stack, Tab, Tabs } from "react-bootstrap";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLink } from "@fortawesome/free-solid-svg-icons/faLink";
import { useState } from "react";
import { useRouter } from "next/navigation";
import GoBackButton from "../components/buttons/GoBackButton";
// import * as Types from "../../utils/types";
// import { useParams } from "next/navigation";
// import { BACKEND_API_URL } from "../../utils/globalValues";

export default function Settings() {
  const [profileSettingState, setProfileSettingState] = useState(true);
  const router = useRouter();
  const handlerBefore = () => {
    router.back();
  };
  const handleToggle1 = () => {
    setProfileSettingState((prev) => !prev);
  };

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <div className="d-flex justify-content-between align-items-center">
        {/* 왼쪽: 뒤로가기 버튼 */}
        <GoBackButton size={30} />

        {/* 중앙: 제목 (m-0으로 기본 마진 제거) */}
        <h5 className="topTitleText m-0">Settings</h5>

        {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
        <div style={{ width: `${30}px` }} />
      </div>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`feed-detail-container`}>
        {/* 본격 사용 가능 공간 */}

        <div
          className={`d-flex justify-content-between align-items-center ${styles.sideArea} mt-3`}
        >
          {/* 왼쪽: 취소 버튼 */}
          <button
            className={`${styles.write} ${styles.exitBtn} custom-button`}
            // onClick={handlerBefore}
            style={{ visibility: "hidden" }}
            aria-hidden="true"
          >
            이전 페이지
          </button>

          {/* 중앙: 제목 */}
          <h6 className={`${styles.createBoxTop} m-0`}>계정 설정</h6>

          {/* 오른쪽: 제목을 중앙에 정렬하기 위한 보이지 않는 공간 */}
          <button
            className={`${styles.write} ${styles.exitBtn} custom-button`}
            style={{ visibility: "hidden" }}
            aria-hidden="true"
          >
            이전 페이지
          </button>
        </div>
        <hr className="feed-underline fontGray4" />
        <Tabs
          defaultActiveKey="Main"
          id="uncontrolled-tab-example"
          variant="underline"
          className="b-3"
          justify
        >
          <Tab eventKey="Main" title="계정" className={``}>
            {/* <hr className="init mb-4 feedUnderLine" /> */}
            <hr className="feed-underline fontGray4 mt-4" />
            <Container className="d-flex align-items-center">
              <Row className="ms-4 me-4 w-100 align-items-center">
                <Col className="">
                  <p className={``}>프로필 비공개</p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <div className={` d-flex align-items-center p-2`}>
                    <Form.Check
                      reverse
                      type="switch"
                      id="custom-switch"
                      checked={!profileSettingState}
                      onChange={handleToggle1}
                      className={``}
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
                  <p
                  // className={`${styles.settingName} ms-5`}
                  // onClick={handleAccountEdit}
                  >
                    계정 비활성화
                  </p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <Link
                    href={"/settings/account-deactivate"}
                    className={` d-flex align-items-center p-2`}
                  >
                    <FontAwesomeIcon
                      icon={faLink}
                      className={`fontGray3 cusorPointer`}
                      // onClick={handleAccountEdit}
                    />
                  </Link>
                </Col>
              </Row>
            </Container>
            <hr className="feed-underline fontGray4 mt-3" />

            <Container className="d-flex align-items-center">
              <Row className="ms-4 me-4 w-100 align-items-center">
                <Col className="">
                  <p
                  // className={`${styles.settingName} ms-5`}
                  // onClick={handleAccountEdit}
                  >
                    계정 삭제
                  </p>
                </Col>
                <Col className="d-flex align-items-center justify-content-end ">
                  <Link
                    href={"/settings/account-close"}
                    className={` d-flex align-items-center p-2`}
                  >
                    <FontAwesomeIcon
                      icon={faLink}
                      className={`fontGray3 cusorPointer`}
                      // onClick={handleAccountEdit}
                    />
                  </Link>
                </Col>
              </Row>
            </Container>
            <hr className="feed-underline fontGray4 mt-3" />
          </Tab>
          <Tab eventKey="Red" title="도움말">
            <hr className="feed-underline fontGray4 mt-3" />
          </Tab>
        </Tabs>
      </div>
    </>
  );
}
