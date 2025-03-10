/* eslint-disable @typescript-eslint/no-explicit-any */

import { ACCESS_TOKEN_NAME, BACKEND_API_URL, REFRESH_TOKEN_NAME } from "./values";

// 쿠키에서 값 가져오기 함수
export function getCookie(name: string) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(";").shift();
  return undefined;
}

// 상태 코드 관련 유틸리티
const isSuccessStatus = (status: number) => [200, 201].includes(status);
// const isBadRequest = (status: number) => status === 400; // 잘못된 요청
const isUnauthorized = (status: number) => status === 401; // 인증 실패
// const isForbidden = (status: number) => status === 403; // 권한 부족
const isNotFound = (status: number) => status === 404; // 리소스 없음

// 토큰 관리 유틸리티
const getAccessToken = () => localStorage.getItem(ACCESS_TOKEN_NAME);
const getRefreshToken = () => getCookie(REFRESH_TOKEN_NAME);
const setAccessToken = (token: string) => localStorage.setItem(ACCESS_TOKEN_NAME, token);
const redirectToLogin = () => window.location.replace("/login");

// 응답 처리 유틸리티
const parseJsonSafely = async (response: Response) => {
  try {
    return await response.json();
  } catch (error) {
    console.error("⚠️ JSON 파싱 실패:", error);
    return null;
  }
};

const handleSuccessResponse = async (response: Response, success: (result: any) => void) => {
  const data = await parseJsonSafely(response);
  console.log("✅ Parsed Response Data:", data);
  success(data);
};

const handleErrorResponse = async (response: Response, fail: () => void) => {
  const errorData = await parseJsonSafely(response);
  console.error(`❌ 요청 실패: ${response.status}`, errorData);
  fail();
};

// 토큰 갱신 관련 로직
const refreshAccessToken = async (refreshToken: string) => {
  console.log("🔄 액세스 토큰 갱신 시도");

  const response = await fetch(`${BACKEND_API_URL}/api/token`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ refreshToken }),
  });

  if (!response.ok) {
    console.error("❌ 토큰 갱신 요청 실패", await response.json());
    throw new Error("Token refresh failed");
  }

  return response.json();
};

const retryOriginalRequest = (
  method: string,
  url: string,
  body: any,
  success: (result: any) => void,
  fail: () => void
) => {
  console.log("🔁 원본 요청 재시도");
  httpRequest(method, url, body, success, fail);
};

const handleUnauthorizedError = async (
  method: string,
  url: string,
  body: any,
  success: (result: any) => void,
  fail: () => void
) => {
  const refreshToken = getRefreshToken();
  if (!refreshToken) {
    console.error("❌ Refresh Token 없음");
    redirectToLogin();
    fail();
    return;
  }

  try {
    const { accessToken } = await refreshAccessToken(refreshToken);
    setAccessToken(accessToken);
    console.log("🔑 새 액세스 토큰 발급 완료");
    retryOriginalRequest(method, url, body, success, fail);
  } catch (error) {
    console.error("🔴 토큰 갱신 실패:", error);
    redirectToLogin();
    fail();
  }
};

// 메인 HTTP 요청 함수
export async function httpRequest(
  method: string,
  url: string,
  body: any,
  success: (result: any) => void,
  fail: () => void
) {
  try {
    const response = await fetch(url, {
      method,
      credentials: "include",
      headers: {
        Authorization: `Bearer ${getAccessToken()}`,
        "Content-Type": "application/json",
      },
      body: body ? JSON.stringify(body) : null,
    });

    if (isSuccessStatus(response.status)) {
      await handleSuccessResponse(response, success);
      return;
    }

    if (isUnauthorized(response.status)) {
      await handleUnauthorizedError(method, url, body, success, fail);
      return;
    }

    if (isNotFound(response.status)) {
      console.warn(`🚫 404 Not Found: ${url}`);
      fail();
      return;
    }

    await handleErrorResponse(response, fail);
  } catch (error) {
    console.error("🚨 요청 중 네트워크 오류:", error);
    fail();
  }
}

// export function httpRequest(
//   method: string,
//   url: string,
//   body: any,
//   success: (result: any) => void,
//   fail: () => void
// ) {
//   let accessToken = localStorage.getItem(ACCESS_TOKEN_NAME);

//   fetch(url, {
//     method,
//     credentials: "include",
//     headers: {
//       Authorization: `Bearer ${accessToken}`,
//       "Content-Type": "application/json",
//     },
//     body: body ? JSON.stringify(body) : null,
//   })
//     .then(async (response) => {
//       // 200 또는 201이면 정상 처리
//       if (response.status === 200 || response.status === 201) {
//         const data = await response.json();
//         console.log("✅ Parsed Response Data:", data); // 로그 추가
//         success(data);
//         return;
//       }

//       // 응답 본문이 있는 경우, JSON을 먼저 파싱
//       let errorData = null;
//       // JSON 타입의 응답인지 확인
//       const contentType = response.headers.get("content-type");
//       if (contentType && contentType.includes("application/json")) {
//         try {
//           errorData = await response.json();
//         } catch (err) {
//           console.error("⚠️ JSON 파싱 실패:", err);
//         }
//       } else {
//         // JSON이 아닌 응답(text/plain, 204 No Content)에서 json() 호출 막음
//         console.warn("⚠️ JSON이 아닌 응답:", await response.text());
//       }

//       // 401 (Unauthorized) 이면서 refresh_token이 있을 경우, 토큰 갱신 시도
//       const refreshToken = getCookie(REFRESH_TOKEN_NAME);
//       if (response.status === 401 && refreshToken) {
//         console.log("🔄 액세스 토큰 만료, 리프레시 토큰으로 재발급 시도");

//         fetch("http://localhost:8090/api/token", {
//           method: "POST",
//           headers: {
//             "Content-Type": "application/json",
//           },
//           body: JSON.stringify({ refreshToken }),
//         })
//           .then(async (res) => {
//             if (!res.ok) {
//               console.error("❌ Refresh token request failed", await res.json());
//               throw new Error("Refresh token request failed");
//             }
//             return res.json();
//           })
//           .then((result) => {
//             console.log("🔑 새 액세스 토큰 발급 완료");
//             localStorage.setItem(ACCESS_TOKEN_NAME, result.accessToken);
//             // 새 토큰으로 요청 재시도
//             httpRequest(method, url, body, success, fail);
//           })
//           .catch((err) => {
//             // window.history.replaceState(null, "", "/login"); // 현재 페이지 url만 변경, 히스토리에 기록, 페이지 이동 x
//             window.location.replace("/login"); // ✅ 즉시 로그인 페이지로 이동 (히스토리 기록 없음)
//             console.error("🔴 토큰 갱신 실패:", err);
//             fail(); // ✅ 리프레시 토큰이 유효하지 않음
//           });
//       } else if (response.status === 401) {
//         // 401 + 리프레시 토큰 없음 → 로그인 페이지로 이동
//         console.error("❌ Refresh Token 없음");
//         window.location.replace("/login");
//         fail();
//       } else if (response.status === 404) {
//         // 🔥 404 (Not Found) → "존재하지 않는 게시물입니다" 메시지 출력
//         console.warn(`🚫 404 Not Found: ${url}`);
//         fail();
//       } else {
//         // 응답 결과가 다른 경우 500 등
//         fail();
//         console.error(`❌ 요청 실패: ${response.status}`, errorData);
//       }
//     })
//     .catch((err) => {
//       console.error("🚨 요청 중 네트워크 오류:", err);
//       fail();
//     });
// }
