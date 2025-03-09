import Image from "next/image";

export default function Footer() {
  return (
    <footer>
      <div className="d-flex flex-column align-items-center justify-content-center mt-5 mb-5">
        {/* <img src="/logoLong.svg" alt="Logo" className="logoLongImg" /> */}

        <Image
          className="d-block"
          src="/logoLong.svg"
          alt="logoLongImg"
          width={350} // 이미지 원본 크기에 맞게 설정
          height={100} // 비율 유지하며 설정
          priority // LCP 최적화를 위해 추가
        />
        <p>All Capybaras ⓒ are so cute.</p>
      </div>
    </footer>
  );
}
