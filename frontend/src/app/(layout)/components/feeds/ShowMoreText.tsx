import { useState, useEffect, useRef } from "react";
import styles from "./ShowMoreText.module.css";

type ShowMoreTextProps = {
  text: string;
  maxLength: number;
  maxLines: number;
};

export default function ShowMoreText({ text, maxLength, maxLines }: ShowMoreTextProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const [isTruncated, setIsTruncated] = useState(false);
  const contentRef = useRef<HTMLParagraphElement>(null);

  useEffect(() => {
    if (contentRef.current) {
      const lineHeight = parseInt(window.getComputedStyle(contentRef.current).lineHeight, 10);
      const maxHeight = lineHeight * maxLines;
      if (contentRef.current.scrollHeight > maxHeight || text.length > maxLength) {
        setIsTruncated(true);
      }
    }
  }, [text, maxLength, maxLines]);

  const toggleText = (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => {
    e.stopPropagation();
    setIsExpanded(!isExpanded);
  };

  const textToShow = isExpanded ? text : text.substring(0, maxLength);

  return (
    <div className={styles.contentWrapper}>
      <p
        ref={contentRef}
        className={`${styles.contentText} ${!isExpanded && isTruncated ? styles.truncated : ""}`}
      >
        {text}
      </p>
      {!isExpanded && isTruncated && (
        <button onClick={toggleText} className={`${styles.moreButton} `}>
          <p className="fontGray2">...더보기</p>
        </button>
      )}
    </div>
  );
}
