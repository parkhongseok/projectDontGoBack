/* eslint-disable @typescript-eslint/no-explicit-any */

import { BACKEND_API_URL } from "./globalValues";

// 상태 코드 관련 유틸리티
const isSuccessStatus = (status: number): boolean => status === 200 || status === 201;
const isUnauthorized = (status: number) => status === 401; // 인증 실패
const isNotFound = (status: number) => status === 404; // 리소스 없음

const redirectToLogin = (): void => {
  // 현재 위치가 이미 로그인 페이지라면 중복 리다이렉션을 방지
  if (window.location.pathname !== "/login") {
    window.location.replace("/login");
  }
};

// 응답 처리 유틸리티
const parseJsonSafely = async (response: Response): Promise<any> => {
  try {
    // 응답 본문이 비어있을 수 있으므로 확인 (e.g., 204 No Content)
    const text = await response.text();
    return text ? JSON.parse(text) : null;
  } catch (error) {
    console.error("⚠️ JSON 파싱 실패:", error);
    return null;
  }
};

// --- Core Logic ---
/**
 * 백엔드에 토큰 재발급을 요청
 * 성공 여부(true/false)만 반환하며, 토큰 자체는 다루지 않음
 * @returns {Promise<boolean>} 토큰 재발급 성공 여부
 */
const refreshAccessToken = async (): Promise<boolean> => {
  console.log("🔄 액세스 토큰 갱신 요청");
  try {
    const response = await fetch(`${BACKEND_API_URL}/token`, {
      // 경로 확인!
      method: "POST", // GET -> POST로 변경
      credentials: "include", // HttpOnly 쿠키(리프레시 토큰)를 보내기 위해 필수!
    });
    return response.ok; // 200-299 상태 코드면 true, 아니면 false
  } catch (error) {
    console.error("🚨 토큰 갱신 중 네트워크 오류:", error);
    return false;
  }
};

/**
 * 모든 API 요청을 처리하는 메인 함수
 * 자동 토큰 갱신 및 재시도 로직이 포함
 */
export async function httpRequest(
  method: string,
  url: string,
  body: any,
  success: (result: any) => void,
  fail: () => void,
  retryCount = 0 // 재시도 횟수 추적을 위한 파라미터 추가
): Promise<void> {
  // 1. 원본 요청 실행
  try {
    const response = await fetch(url, {
      method,
      credentials: "include", // ✨ 모든 요청에 HttpOnly 쿠키를 자동으로 포함시킴
      headers: {
        "Content-Type": "application/json",
      },
      // 🗑️ 'Authorization' 헤더는 이제 필요 없습니다.
      body: body ? JSON.stringify(body) : null,
    });

    // 2. 응답 상태에 따른 분기 처리
    if (isSuccessStatus(response.status)) {
      const data = await parseJsonSafely(response);
      console.log("✅ 요청 성공:", data);
      success(data);
      return;
    }

    if (isUnauthorized(response.status)) {
      console.warn("❗ 401 Unauthorized. 토큰 갱신을 시도합니다.");

      // 재시도 횟수 제한 (무한 루프 방지)
      if (retryCount > 0) {
        console.error("🔴 토큰 갱신 후에도 인증 실패. 로그인 페이지로 이동합니다.");
        redirectToLogin();
        return;
      }

      // 3. 토큰 갱신 및 원본 요청 재시도
      const isRefreshSuccess = await refreshAccessToken();

      if (isRefreshSuccess) {
        console.log("✅ 토큰 갱신 성공. 원본 요청을 재시도합니다.");
        await httpRequest(method, url, body, success, fail, retryCount + 1);
      } else {
        console.error("🔴 최종 토큰 갱신 실패. 로그인 페이지로 이동합니다.");
        redirectToLogin();
      }
      return;
    }

    if (isNotFound(response.status)) {
      console.warn(`🚫 404 Not Found: ${url}`);
      fail();
      return;
    }

    // 그 외 4xx, 5xx 에러 처리
    const errorData = await parseJsonSafely(response);
    console.error(`❌ 요청 실패: ${response.status}`, errorData);
    fail();
  } catch (error) {
    console.error("🚨 요청 중 심각한 네트워크 오류:", error);
    fail();
  }
}
