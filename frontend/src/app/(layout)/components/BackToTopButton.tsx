"use client";

import { useEffect, useState } from "react";
import styles from "./BackToTopButton.module.css";

export default function BackToTopButton() {
  // 버튼이 보여야 하는지 여부를 결정하는 상태
  const [isVisible, setIsVisible] = useState(false);

  // 스크롤 위치를 감지하는 함수
  const handleScroll = () => {
    // window.scrollY가 300px보다 크면 버튼을 보이게 함
    if (window.scrollY > 300) {
      setIsVisible(true);
    } else {
      setIsVisible(false);
    }
  };

  // 페이지 최상단으로 스크롤하는 함수
  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth", // 부드러운 스크롤 효과
    });
  };

  // 컴포넌트가 마운트될 때 스크롤 이벤트 리스너 추가
  useEffect(() => {
    window.addEventListener("scroll", handleScroll);

    // 컴포넌트가 언마운트될 때 이벤트 리스너 제거 (메모리 누수 방지)
    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, []);

  return (
    // isVisible 상태가 true일 때만 버튼을 렌더링
    isVisible && (
      <button className={styles.topButton} onClick={scrollToTop} title="맨 위로 이동">
        {/* 화살표 아이콘 (SVG) */}
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="M12 19V5M5 12l7-7 7 7" />
        </svg>
      </button>
    )
  );
}