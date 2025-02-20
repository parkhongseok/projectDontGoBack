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

    const updateUserContext = async ()=>{
        const url = "http://localhost:8090/api/v1/users/me";
        const body = null; 
        const success = (result: any) => {
            setUserContext(result.data);
        };
        const fail = () => {    
            localStorage.setItem("access_token", "");
            console.log("문제발생")
            window.location.href = "/login";
        };
        httpRequest("GET", url, body, success, fail);
    }

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
