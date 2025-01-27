'use client'

import { useEffect, useState } from 'react';
import {Container, Row, Col} from 'react-bootstrap';



export default function Post() {
  const [post, setPost] = useState(
    {content: 'content1'}
  )

  useEffect(() => {
    fetch("http://localhost:8090/api/v1/posts/1")
    .then(response=>response.json())
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
          <Row className='mt-5'/>
          <Row>
          <Col>{post.content}</Col>
          </Row>
        </Container>
    </>
  );
}
