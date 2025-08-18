"use client";

import { useRouter } from "next/navigation";
import styles from "./GoBackButton.module.css";

// 컴포넌트가 받을 props 타입 정의
type GoBackButtonProps = {
  className?: string; // 추가적인 스타일링을 위한 className
  color?: string;     // 아이콘 색상 (CSS 변수 사용 가능)
  size?: number;      // 버튼 크기
};

export default function GoBackButton({
  className = "",
  color = "var(--bs-body-color, #212529)", // Bootstrap의 기본 텍스트 색상을 기본값으로 사용
  size = 40,
}: GoBackButtonProps) {
  const router = useRouter();

  const handleGoBack = () => {
    router.back();
  };

  // props로 받은 size와 color를 인라인 스타일에 적용
  const buttonStyle = {
    width: `${size}px`,
    height: `${size}px`,
    color: color, // 이 color 값이 SVG의 currentColor로 전달됩니다.
  };

  return (
    <button
      className={`${styles.backButton} ${className}`}
      style={buttonStyle}
      onClick={handleGoBack}
      title="뒤로가기"
    >
      {/* 둥근 느낌의 깔끔한 '<' 아이콘 (SVG) */}
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="24"
        height="24"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor" // 이 값은 CSS의 color 속성으로 덮어씌워집니다.
        strokeWidth="2.5" // 선을 약간 두껍게 하여 부드러운 느낌 강조
        strokeLinecap="round" // 선의 끝을 둥글게
        strokeLinejoin="round" // 선이 만나는 지점을 둥글게
      >
        <path d="M15 18l-6-6 6-6" />
      </svg>
    </button>
  );
}