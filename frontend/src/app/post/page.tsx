'use client'

import { useEffect, useState } from 'react';
import {Container, Row, Col} from 'react-bootstrap';



export default function Post() {
  const [post, setPost] = useState([
    {content: 'content1'},
    {content: 'content2'}
  ])

  useEffect(() => {
    fetch("http://localhost:8090/api/v1/posts")
    .then(response=>response.json())
    // .then(response=>response.data)
    // .then(response => response.posts)
    .then((result)=>{
      console.log(result);
      setPost(result)
    })
  }, [])

  return (
    <>
          <Container>
          <Row className='mt-5'/>
          <Row className='mt-5'/>
          hihihihi
          <Row className='mt-5'/>
          <Row>
            {
              post.map((item, idx) => {
                return (
                  <Row className='mt-5' key={idx}>
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
