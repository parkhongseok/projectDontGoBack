import { useRouter } from "next/navigation";
import { httpRequest } from "../utils/httpRequest";
import * as Types from "../utils/types";
import React, { createContext, useState, useContext, ReactNode } from "react";
import { ACCESS_TOKEN_NAME, BACKEND_API_URL } from "../utils/values";

interface UserContextType {
  userContext: Types.User | null;
  setUserContext: (value: React.SetStateAction<Types.User | null>) => void;
  fetchUserContext: (token?: string) => Promise<void>;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider = ({ children }: { children: ReactNode }) => {
  const [userContext, setUserContext] = useState<Types.User | null>(null);
  const router = useRouter();

  const fetchUserContext = async () => {
    const url = `${BACKEND_API_URL}/api/v1/users/me`;
    const body = null;
    const success = (result: Types.ResData<Types.User>) => {
      setUserContext(result.data);
    };
    const fail = () => {
      console.warn("[fetchUserContext] 유저 로딩 실패 함수 호출");
      localStorage.setItem(`${ACCESS_TOKEN_NAME}`, "");
      // console.error("토큰 초기화 및 재로그인");
      router.replace("/login"); // 뒤로가기 방지 이동
    };
    httpRequest("GET", url, body, success, fail);
  };

  return (
    <UserContext.Provider value={{ userContext, setUserContext, fetchUserContext }}>
      {children}
    </UserContext.Provider>
  );
};

export const useUser = () => {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("useUser must be used within a UserProvider");
  }
  return context;
};
