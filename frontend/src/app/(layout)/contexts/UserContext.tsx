import { useRouter } from "next/router";
// import Dummys from "../utils/dummyData";
import { httpRequest } from "../utils/httpRequest";
import * as Types from "../utils/types"
import React, { createContext, useState, useContext, ReactNode } from 'react';


interface UserContextType {
    userContext:  Types.User | null;
    setUserContext: (value: React.SetStateAction<Types.User | null>) => void
    updateUserContext: (token?: string) => Promise<void>;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider = ({ children }: { children: ReactNode }) => {
    const [userContext, setUserContext] = useState<Types.User | null>(null);

    const updateUserContext = async (token? : string|null) => {
        const url = "http://localhost:8090/api/v1/users/me";
        const body = null; 
        const success = (result: any) => {
            setUserContext(result.data);
        };
        // 로그인 실패 시 기존 액세스 토큰 폐기
        const fail = () => {    
            // localStorage.removeItem("access_token");
            // window.location.href = "/login";  
        };
        if (token){
            httpRequest("GET", url, body, success, fail, token);
        }else{
            httpRequest("GET", url, body, success, fail);
        }
        
    };

    return (
        <UserContext.Provider value={{ userContext, setUserContext, updateUserContext }}>
            {children}
        </UserContext.Provider>
    );
}

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};
