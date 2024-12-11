'use client'

import { useEffect, useState } from 'react';
import {Container, Row, Col} from 'react-bootstrap';
// import { json } from 'stream/consumers';


export default function Link() {
  let [data, setData]= useState(['hi'])
  fetch("http://localhost:8090/api/v1/articles")

  return (
    <>

          <Container>
          <Row className='mt-5'/>
          <Row className='mt-5'/>
          <Row className='mt-5'/>
          <Row>
            <Col></Col>
            <Col className='px-5'>
            <button >BUTTON</button>
            </Col>
            <Col>{data[0]}</Col>
          </Row>
        </Container>
    </>
  );
}
