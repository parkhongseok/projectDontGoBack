'use client'

import { useEffect, useState } from 'react';
import {Container, Row, Col} from 'react-bootstrap';
// import { json } from 'stream/consumers';


export default function Link() {
  // let [data, setData]= useState(['hi'])
  // fetch("http://localhost:8090/api/v1/articles")
  let [data, setData] = useState(
    [
    {title: 'tilte1', content: 'content1'},
    {title: 'tilte2', content: 'content2'}
  ])

  useEffect(() => {
    fetch("http://localhost:8090/api/v1/articles")
    .then(response=>response.json())
    .then(response=>response.data)
    .then(response => response.articles)
    .then((result)=>{
      console.log(result)
      setData(result
      //   (result)=>{
      //   let newData = [...result]
      //   return newData
      // }
    )
    })
  }, [])

  return (
    <>
          <Container>
          <Row className='mt-5'/>
          <Row className='mt-5'/>
          <Row className='mt-5'/>
          {/* <Row>
            <Col>{data[0].title}</Col>
            <Col>{data[0].content}</Col>
            <Col>{data[1].title}</Col>
            <Col>{data[1].content}</Col>
            <Col className='px-5'>
            <button >BUTTON</button>
            </Col>
            <Col></Col>
          </Row> */}

          <Row>
            {
              data.map((item, idx) => {
                return (
                  <Row className='mt-5' key={idx}>
                    <Col>{item.title}</Col>
                    <Col>{item.content}</Col>
                  </Row>
                )
              })
            }
          </Row>
        </Container>
    </>
  );
}
