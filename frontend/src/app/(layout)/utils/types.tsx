export type ResData<T> = {
  data: T;
};

export type Feed = {
  feedId: number;
  userId: number;
  content: string;
  author: string;
  userRole: 'ADMIN' | 'USER' | 'GUEST';
  feedType: 'RED' | 'BLUE';
  likeCount: number;
  commentCount: number;
  isLiked?: boolean;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
};

export type User = {
  userId: number;
  email: string;
  profileVisibility: string;
  userName: string;
  userType: 'RED' | 'BLUE';
  userRole: 'ADMIN' | 'USER' | 'GUEST';
};

export type Comment = {
  commentId: number;
  feedId: number;
  userId: number;
  author: string;
  userRole: 'ADMIN' | 'USER' | 'GUEST';
  content: string;
  commentType: 'RED' | 'BLUE';
  likeCount: number;
  subCommentCount: number;
  isLiked?: boolean;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
};


/**
 * 차트의 개별 데이터 포인트
 */
export type AssetHistoryPoint = {
  date: string;
  amount: number;
  changeAmount: number | null; // 첫 값은 null일 수 있으므로
  changePercent: number | null; // 첫 값은 null일 수 있으므로
  type: 'RED' | 'BLUE'
};

/**
 * 백엔드로부터 받는 시계열 데이터 전체 응답 DTO
 */
export type AssetHistorySeries = {
  userId: number;
  from: string; // "YYYY-MM-DD"
  to: string; // "YYYY-MM-DD"
  interval: string;
  points: AssetHistoryPoint[];
  latestChangedAt: string; // ISO 8601 형식의 날짜-시간 문자열
  count: number;
};

export type Notification = {
  id: number;
  userImage: string;
  userName: string;
  action: string;
  createdAt: string;
};
