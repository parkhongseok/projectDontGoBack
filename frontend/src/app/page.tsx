'use client'

import Post from "./components/Post";
import "./globals.css";
import {Container, Row, Col, Card} from 'react-bootstrap';
export default function Home() {
  return (
    <div>
      {/* dropdown 버튼이 들어올 자리 */}
      <h6 className="text-center mb-4 pt-3">Post</h6>
      <Container className="posts-container">
          <Row>
            <Col >
              {/* 사이드바가 차지하지 않는 나머지 공간 */}
              <Post></Post>
            </Col>
          </Row>
        </Container>

    </div>
  );
}
