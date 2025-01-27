'use client'

import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';


export default function Header(){

  return(
      <div>
      <Navbar>
        <Container>
          <Navbar.Brand href="/">DG</Navbar.Brand>
          <Nav className="me-auto">
            <Nav.Link href="/post">Post All</Nav.Link>
            <Nav.Link href="/hamberger">Post One</Nav.Link>
            <Nav.Link href="#pricing">Pricing</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    </div>
  )
}