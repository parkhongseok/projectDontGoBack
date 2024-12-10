'use client'
import styles from "./page.module.css";
// import {Container, Nav, Navbar, NavDropdown} from 'react-bootstrap';
// import Container from 'react-bootstrap/Container';
// import Row from 'react-bootstrap/Row';
// import Col from 'react-bootstrap/Col';
import {Container, Navbar, Row, Col} from 'react-bootstrap';

export default function Home() {
  return (
    <>
    <Container>
      <Row>
        <Col></Col>
        <Col>d</Col>
        <Col></Col>
      </Row>
    </Container>
    <Navbar className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Brand link</Navbar.Brand>
      </Container>
    </Navbar>
    </>


    // <div className={styles.page}>
    //   <main className={styles.main}>
    //     <h1 className="text-danger">what the hell</h1>
    //   </main>
    //   <footer className={styles.footer}>
    //   </footer>
    // </div>
  );
}
