import Dummys from "../utils/dummyData";
import * as Types from "../utils/types"
import React, { createContext, useState, useContext, ReactNode } from 'react';


interface UserContextType {
    userContext:  Types.User | null;
    setUserContext: (user : Types.User) => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export const UserProvider = ({ children }: { children: ReactNode }) => {
    const [userContext, setUserContext] = useState<Types.User | null>(Dummys.User);

    return (
        <UserContext.Provider value={{ userContext, setUserContext }}>
            {children}
        </UserContext.Provider>
    );
};

export const useUser = () => {
    const context = useContext(UserContext);
    if (!context) {
        throw new Error('useUser must be used within a UserProvider');
    }
    return context;
};
