export type Feed = {
  feedId: number;
  userId: number;
  content: string;
  userName: string;
  feedType: string;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt?: string;
  deletedAt?: string;
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
  content: string;
  userName: string;
  commentType: string;
  likeCount: number;
  commentCount: number;
  createdAt: string;
  updatedAt?: string;
};
