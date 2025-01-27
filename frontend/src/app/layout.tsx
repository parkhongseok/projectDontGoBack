import "./globals.css";
import { Container, Row, Col } from 'react-bootstrap';
import SideBar from "./components/Sidebar";
export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {

  return (
    <html lang="en">
      <body className="layout-home">
        <SideBar/>
        {children}


      </body>
    </html>
  );
}
