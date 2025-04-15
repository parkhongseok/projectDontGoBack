"use client";

import "../globals.css";

import { Col, Container, Form, Row, Tab, Tabs } from "react-bootstrap";
import Link from "next/link";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faLink } from "@fortawesome/free-solid-svg-icons/faLink";
import { useState } from "react";
// import * as Types from "../../utils/types";
// import { useParams } from "next/navigation";
// import { BACKEND_API_URL } from "../../utils/globalValues";

export default function Settings() {
  const [profileSettingState, setProfileSettingState] = useState(true);

  const handleToggle1 = () => {
    setProfileSettingState((prev) => !prev);
  };

  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">Settings</h5>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}
      <div className={`pt-4 feed-detail-container`}>
        {/* 본격 사용 가능 공간 */}
        <Tabs
          defaultActiveKey="Main"
          id="uncontrolled-tab-example"
          variant="underline"
          className="mb-3"
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
                  <Link href={"/settings"} className={` d-flex align-items-center p-2`}>
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
                    href={"/settings/close-account"}
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
