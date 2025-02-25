import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/ko";
// 플러그인 적용 및 언어 설정
dayjs.extend(relativeTime);
dayjs.locale("ko");

// 상대적 시간 변환 함수
export const timeAgo = (createdAt: string, updatedAt: string) => {
  const now = dayjs();
  const created = dayjs(createdAt);
  const updated = dayjs(updatedAt);
  let past;
  let isUpdated = false;

  if (created >= updated) {
    past = created;
  } else {
    past = updated;
    isUpdated = true;
  }

  const diffInSeconds = now.diff(past, "second");

  if (diffInSeconds < 60) {
    return isUpdated ? "방금 전 수정됨" : "방금 전";
  }

  let result = past.fromNow();

  return isUpdated ? `${result} 수정됨` : result;
};
