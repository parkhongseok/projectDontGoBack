"use client";
import { Badge, Col, Container, Row } from "react-bootstrap";
import styles from "../Feed.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGear } from "@fortawesome/free-solid-svg-icons";
import * as Types from "../../utils/types";
import { useState } from "react";
import ProfileSetting from "./ProfileSetting";

type PropsType = {
  user: Types.User | null;
};
export default function CreateFeed({ user }: PropsType) {
  const [isSettingOpen, setIsSettingOpen] = useState(false);

  const handleSetting = () => {
    setIsSettingOpen(true);
  };

  const badgeType = user?.userType == "RED" ? "danger" : "primary";
  const badgeName = user?.userType == "RED" ? "RED" : "BLUE";

  if (!user) return <div className="loading mt-4"></div>;
  const typeClass = styles[user?.userType] || "";
  return (
    <>
      {isSettingOpen && <ProfileSetting setIsSettingOpen={setIsSettingOpen} />}
      <div className="profileTop mb-3 ">
        <Container className="mt-3">
          <Row className="mb-3 ms-3">
            <Col className="d-flex align-items-center">
              <Badge pill bg={`${badgeType}`} className={`${styles.smallBadge} `} as={"span"}>
                {badgeName}
              </Badge>
              <p className={`${styles.ProfileuserName} ${typeClass} mx-2`}>{user.userName}</p>
            </Col>
            <Col className={`${styles.profileSettingContainer} me-4`}>
              <FontAwesomeIcon
                icon={faGear}
                className={`${styles.profileSetting}`}
                onClick={handleSetting}
              />
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
function setFeedsLoading(arg0: boolean) {
  throw new Error("Function not implemented.");
}
