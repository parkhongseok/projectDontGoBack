import "./globals.css";
import SideBar from "./components/Sidebar";
// import Header from "./components/Header";
export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {

  return (
    <html lang="en">
      <body className="layout-home">
        <SideBar/>
        {/* <Header/> */}
        <div>
          {children}
        </div>


      </body>
    </html>
  );
}
