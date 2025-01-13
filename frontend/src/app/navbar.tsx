'use client'

// import { useEffect } from "react";
// import Script from 'next/script';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
// import 'bootstrap/dist/js/bootstrap.bundle.min.js';

export default function MyNavbar(){
  // useEffect(() => {
  //   require("bootstrap/dist/js/bootstrap.bundle.min.js");
  // }, []);
    // useEffect(() => {
    //   import('bootstrap/dist/js/bootstrap.bundle.min.ts');
    // }, []);
  return(
      <>
      <Navbar bg="light" data-bs-theme="light">
        <Container>
          <Navbar.Brand href="/">Dont Go Back</Navbar.Brand>
          <Nav className="me-auto">
            <Nav.Link href="/Link">LINK</Nav.Link>
            <Nav.Link href="#features">Features</Nav.Link>
            <Nav.Link href="#pricing">Pricing</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </>
  )
}