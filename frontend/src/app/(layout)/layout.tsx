'use client'

import "./globals.css";
import SideBar from "../(layout)/components/Sidebar";
import { FeedProvider } from "./contexts/FeedContext";
import { UserProvider } from "./contexts/UserContext";
// import { useEffect } from "react";

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {

  return (
    
    <html lang="en">
        <body >
          <UserProvider>
            <FeedProvider>
              <SideBar/>
              {/* <Header/> */}
              <div className="main-layout">
                <div className="sidebar-space"/>
                <div className="main-space">
                {/* Provider 이새끼가 자동으로 overflow hidden 설정을 만드는 거 같은데 왜지 */}
                  {children}
                </div>
              </div>
            </FeedProvider>
          </UserProvider>
        </body>
      </html>
  );
}
