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
    <Navbar className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">Brand link</Navbar.Brand>
      </Container>
    </Navbar>
    {
      [1,2,3,4,5,6,7,8,9].map((i) =>{
        return (

          <Container key={i}>
          <Row>
            <Col></Col>
            <Col>
            {i%2==1 ? <h1 className="text-danger">HELP</h1>:<Col></Col>}
            </Col>
            <Col></Col>
          </Row>
          <Row>
            <Col>            
            {i%2==0 ? <h1 className="text-danger">HELP</h1>:<Col></Col>}
            </Col>
            <Col></Col>
            <Col></Col>
          </Row>
          <Row>
            <Col></Col>
            <Col></Col>
            <Col>            
            {i%2==1 ? <h1 className="text-danger">HELP</h1>:<Col></Col>}
            </Col>
          </Row>
        </Container>
        )
        })
      
    }


    <div className={styles.page}>
      <main className={styles.main}>
        <h1 className="text-danger">HELP</h1>
      </main>
      <footer className={styles.footer}>
      </footer>
    </div>
    </>
  );
}
