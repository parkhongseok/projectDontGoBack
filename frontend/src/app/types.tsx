export type Feed = {
  feedId: number;
  userId: number;
  userName: string;
  feedType: string;
  beforeTime: string;
  content: string;
  likeCount: number;
  commentCount: number;
};

export type User = {
  userId: number;
  email: string;
  profileVisibility: string;
  userName: string;
  userType: string;
};

export type Comment = {
  commentId: number;
  feedId: number;
  userId: number;
  userName: string;
  commentType: string;
  beforeTime: string;
  content: string;
  likeCount: number;
  commentCount: number;
};

// ✅ 여기서 타입을 적용함
export const Types = {
  Feed: {} as Feed,
  User: {} as User,
  Comment: {} as Comment,
} as const;
