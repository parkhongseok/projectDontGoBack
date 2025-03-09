export type Feed = {
  feedId: number;
  userId: number;
  content: string;
  author: string;
  feedType: string;
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
  userType: string;
};

export type Comment = {
  commentId: number;
  feedId: number;
  userId: number;
  author: string;
  content: string;
  commentType: string;
  likeCount: number;
  subCommentCount: number;
  isLiked?: boolean;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
};

export type ResData<T> = {
  data: T;
};
