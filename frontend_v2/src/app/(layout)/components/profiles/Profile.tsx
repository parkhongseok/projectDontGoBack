"use client";
import { Badge, Col, Container, OverlayTrigger, Row, Tooltip } from "react-bootstrap";
import styles from "../Feed.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGear } from "@fortawesome/free-solid-svg-icons";
import * as Types from "../../utils/types";
import { useState } from "react";
import ProfileSetting from "./ProfileSetting";
import { useUser } from "../../contexts/UserContext";

type PropsType = {
  userProps: Types.User | null;
};
export default function CreateFeed({ userProps }: PropsType) {
  const [isSettingOpen, setIsSettingOpen] = useState(false);
  const { userContext } = useUser();
  const handleSetting = () => {
    setIsSettingOpen(true);
  };

  const badgeType = userProps?.userType == "RED" ? "danger" : "primary";
  const badgeName = userProps?.userType == "RED" ? "RED" : "BLUE";

  if (!userProps) return <div className="loading mt-4"></div>;
  const typeClass = styles[userProps?.userType] || "";
  return (
    <>
      {isSettingOpen && <ProfileSetting setIsSettingOpen={setIsSettingOpen} />}
      <div className="profileTop mb-3 ">
        <Container className="mt-3">
          <Row className="mb-3 ms-3">
            <OverlayTrigger
              key={"key"}
              placement={"left"}
              overlay={
                <Tooltip id={`button-tooltip`}>
                  <strong>{"임의"}</strong>로 지정된 값입니다!
                </Tooltip>
              }
            >
              <Col className="d-flex align-items-center">
                <Badge pill bg={`${badgeType}`} className={`${styles.smallBadge} `} as={"span"}>
                  {badgeName}
                </Badge>
                <p className={`${styles.ProfileuserName} ${typeClass} mx-2`}>
                  {userProps.userName}
                </p>
              </Col>
            </OverlayTrigger>
            <Col className={`${styles.profileSettingContainer} me-4`}>
              {userContext?.userId == userProps.userId && (
                <FontAwesomeIcon
                  icon={faGear}
                  className={`${styles.profileSetting}`}
                  onClick={handleSetting}
                />
              )}
            </Col>
          </Row>
          <Row className="mb-3 ms-3">
            <Col xs={6}>{/* <p>프로필 메시지인데 뭐라고 쓸까</p> */}</Col>
            <Col></Col>
            <Col></Col>
          </Row>
          {/* <Row>
            <Button variant="secondary" className={`${styles.profileSettingBtn}`}>
              프로필 설정
            </Button>
          </Row> */}
        </Container>
      </div>
    </>
  );
}
