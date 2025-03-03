"use client";

import styles from "../Feed.module.css";
import { Carousel } from "react-bootstrap";

export default function Chart() {
  return (
    <>
      <Carousel
        as={"div"}
        data-bs-theme="dark"
        slide={false}
        className="mx-auto d-block w-100 px-4 mb-4"
      >
        <Carousel.Item>
          <div className={`${styles.chartImgContainer}`}>
            <img className="d-block w-100 " src="/chart_week.png" alt="First slide" />
          </div>
          <Carousel.Caption>
            {/* <h3 className="fontGray3">Week </h3>
            <p className="fontGray3">Nulla vitae elit libero, a pharetra augue mollis interdum.</p> */}
          </Carousel.Caption>
        </Carousel.Item>
        <Carousel.Item>
          <div className={`${styles.chartImgContainer}`}>
            <img className="d-block w-100 " src="/chart_year.png" alt="First slide" />
          </div>
          <Carousel.Caption>
            {/* <h3 className="fontGray3">Year </h3>
            <p className="fontGray3">Nulla vitae elit libero, a pharetra augue mollis interdum.</p> */}
          </Carousel.Caption>
        </Carousel.Item>
        {/* <Carousel.Item>

          <Carousel.Caption>
            <h3>Second slide label</h3>
            <p>Lorem ipsum dolor sit amet, consectetur adipiscing elit.</p>
          </Carousel.Caption>
        </Carousel.Item>
        <Carousel.Item>

          <Carousel.Caption>
            <h3>Third slide label</h3>
            <p>Praesent commodo cursus magna, vel scelerisque nisl consectetur.</p>
          </Carousel.Caption>
        </Carousel.Item> */}
      </Carousel>
    </>
  );
}
