import "../globals.css";

export default function Loading() {
  return (
    <>
      {/* dropdown 버튼이 들어올 자리 */}
      <h5 className="text-center mb-4 pt-4 topTitleText">Hello World</h5>

      {/* 사이드바가 차지하지 않는 나머지 공간 */}

      <div className="p-4 feeds-container">
        <>
          <div className="p-4 loadingFeedContainer mb-5">
            <div className="d-flex align-items-center pb-1">
              <div className="loadingProfile"></div>
              <div className="loadingTitle ms-3"></div>
            </div>
            <div className="ms-5 mb-2">
              <div className="loadingContant ms-2 mb-3"></div>
              <div className="loadingContant ms-2"></div>
            </div>
          </div>
        </>
        <>
          <div className="p-4 loadingFeedContainer mb-5">
            <div className="d-flex align-items-center pb-1">
              <div className="loadingProfile"></div>
              <div className="loadingTitle ms-3"></div>
            </div>
            <div className="ms-5 mb-2">
              <div className="loadingContant ms-2 mb-3"></div>
              <div className="loadingContant ms-2"></div>
            </div>
          </div>
        </>
        <>
          <div className="p-4 loadingFeedContainer mb-5">
            <div className="d-flex align-items-center pb-1">
              <div className="loadingProfile"></div>
              <div className="loadingTitle ms-3"></div>
            </div>
            <div className="ms-5 mb-2">
              <div className="loadingContant ms-2 mb-3"></div>
              <div className="loadingContant ms-2"></div>
            </div>
          </div>
        </>
        <>
          <div className="p-4 loadingFeedContainer mb-5">
            <div className="d-flex align-items-center pb-1">
              <div className="loadingProfile"></div>
              <div className="loadingTitle ms-3"></div>
            </div>
            <div className="ms-5 mb-2">
              <div className="loadingContant ms-2 mb-3"></div>
              <div className="loadingContant ms-2"></div>
            </div>
          </div>
        </>
      </div>
    </>
  );
}
