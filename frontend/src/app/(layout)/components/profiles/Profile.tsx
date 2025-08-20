"use client";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import styles from "../feeds/Feed.module.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faGear } from "@fortawesome/free-solid-svg-icons";
import * as Types from "../../utils/types";
import { useState } from "react";
import ProfileSetting from "./ProfileSetting";
import { useUser } from "../../contexts/UserContext";
import BadgeMe from "../badge/BadgeMe";
import Badge from "../badge/Badge";

type PropsType = {
  userProps: Types.User | null;
};
export default function CreateFeed({ userProps }: PropsType) {
  const [isSettingOpen, setIsSettingOpen] = useState(false);
  const { userContext } = useUser();
  const handleSetting = () => {
    setIsSettingOpen(true);
  };

  if (!userProps) return <div className="loading mt-4"></div>;

  // 뱃지를 렌더링하는 헬퍼 함수
  const renderBadge = () => {
    return (
      <>
        {/* 역할(Role) 기반 뱃지 */}
        {userProps.userRole === "ADMIN" && <Badge role="admin">관리자</Badge>}
        {userProps.userRole === "GUEST" && <Badge role="guest">방문자</Badge>}

        {/* '나' 뱃지 (역할과 별개로 항상 표시) */}
        {userProps.userId === userContext?.userId && <BadgeMe role="me">나</BadgeMe>}
      </>
    );
  };

  const typeClass = styles[userProps?.userType] || "";
  return (
    <>
      {isSettingOpen && <ProfileSetting setIsSettingOpen={setIsSettingOpen} />}
      <div className="profileTop mb-3 ">
        <div className="mt-3">
          <div className="mb-3 ms-3 d-flex align-items-center">
            <OverlayTrigger
              key={"key"}
              placement={"left"}
              overlay={
                <Tooltip id={`button-tooltip`}>
                  <strong>{"매일"}</strong> 값이 변합니다.
                </Tooltip>
              }
            >
              <div className="d-flex align-items-center">
                {/* 이름과 뱃지를 정렬하기 위한 div */}
                <p className={`${styles.ProfileuserName} ${typeClass} cusorPointer mb-0`}>
                  {userProps.userName}
                </p>
                {renderBadge()} {/* 헬퍼 함수 호출 */}
              </div>
            </OverlayTrigger>
            <div className={`${styles.profileSettingContainer}`}>
              {userContext?.userId == userProps.userId && (
                <FontAwesomeIcon
                  icon={faGear}
                  className={`${styles.profileSetting}`}
                  onClick={handleSetting}
                />
              )}
            </div>
          </div>

          {/* <Row>
            <Button variant="secondary" className={`${styles.profileSettingBtn}`}>
              프로필 설정
            </Button>
          </Row> */}
        </div>
      </div>
    </>
  );
}
