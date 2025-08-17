"use client";

import "./globals.css";

import SideBar from "../(layout)/components/Sidebar";
import { FeedProvider } from "./contexts/FeedContext";
import { UserProvider } from "./contexts/UserContext";

// fontAwesome
import { config } from "@fortawesome/fontawesome-svg-core";
import "@fortawesome/fontawesome-svg-core/styles.css"; // CSS 직접 불러오기

config.autoAddCss = false;

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>
        {/* <body className={`${geistSans.variable} ${geistMono.variable}`}> */}
        <UserProvider>
          <FeedProvider>
            <SideBar />
            {/* <Header/> */}
            <div className="main-layout">
              <div className="sidebar-space" />
              <div className="main-space">{children}</div>
            </div>
          </FeedProvider>
        </UserProvider>
      </body>
    </html>
  );
}
