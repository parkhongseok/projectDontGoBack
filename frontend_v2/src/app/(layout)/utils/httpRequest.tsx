// 쿠키에서 값 가져오기 함수
export function getCookie(name: string) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(";").shift();
  return undefined;
}

export function httpRequest(
  method: string,
  url: string,
  body: any,
  success: (result: any) => void,
  fail: () => void
) {
  let accessToken = localStorage.getItem("access_token");

  fetch(url, {
    method,
    credentials: "include",
    headers: {
      Authorization: `Bearer ${accessToken}`,
      "Content-Type": "application/json",
    },
    body: body ? JSON.stringify(body) : null,
  })
    .then(async (response) => {
      // 200 또는 201이면 정상 처리
      if (response.status === 200 || response.status === 201) {
        const data = await response.json();
        console.log("✅ Parsed Response Data:", data); // 로그 추가
        success(data);
        return;
      }

      // 응답 본문이 있는 경우, JSON을 먼저 파싱
      let errorData = null;
      // JSON 타입의 응답인지 확인
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        try {
          errorData = await response.json();
        } catch (err) {
          console.error("⚠️ JSON 파싱 실패:", err);
        }
      } else {
        // JSON이 아닌 응답(text/plain, 204 No Content)에서 json() 호출 막음
        console.warn("⚠️ JSON이 아닌 응답:", await response.text());
      }

      // 401 (Unauthorized) 이면서 refresh_token이 있을 경우, 토큰 갱신 시도
      const refreshToken = getCookie("refresh_token");
      if (response.status === 401 && refreshToken) {
        console.log("🔄 액세스 토큰 만료, 리프레시 토큰으로 재발급 시도");

        fetch("http://localhost:8090/api/token", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ refreshToken }),
        })
          .then(async (res) => {
            if (!res.ok) {
              console.error("❌ Refresh token request failed", await res.json());
              throw new Error("Refresh token request failed");
            }
            return res.json();
          })
          .then((result) => {
            console.log("🔑 새 액세스 토큰 발급 완료");
            localStorage.setItem("access_token", result.accessToken);
            // 새 토큰으로 요청 재시도
            httpRequest(method, url, body, success, fail);
          })
          .catch((err) => {
            // window.history.replaceState(null, "", "/login"); // 현재 페이지 url만 변경, 히스토리에 기록, 페이지 이동 x
            window.location.replace("/login"); // ✅ 즉시 로그인 페이지로 이동 (히스토리 기록 없음)
            console.error("🔴 토큰 갱신 실패:", err);
            fail(); // ✅ 리프레시 토큰이 유효하지 않음
          });
      } else if (!refreshToken) {
        // 토큰이 없는 경우
        console.error("❌ Refresh Token 없음");
        window.location.replace("/login"); // ✅ 즉시 로그인 페이지로 이동 (히스토리 기록 없음)
        fail(); // ✅ 리프레시 토큰이 없음
      } else {
        // 응답 결과가 다른 경우 500 등
        fail();
        console.error(`❌ 요청 실패: ${response.status}`, errorData);
      }
    })
    .catch((err) => {
      console.error("🚨 요청 중 네트워크 오류:", err);
      fail();
    });
}

// // HTTP 요청 보내는 함수
// export function httpRequest(
//     method: string,
//     url: string,
//     body: any,
//     success: (result: any) => void,
//     fail: () => void,
//      // 파라미터로 토큰을 받을 수 있음 (로컬스토리지에 저장 전에 미리 유저 요청 시 사용)
//      // 파라미터가 없다면 로컬 스토리지에서 액세스 토큰 가져오기
//     accessToken? : string | null
// ) {
//     accessToken = accessToken || localStorage.getItem('access_token')
//     fetch(url, {
//         method: method,
//         credentials: 'include',
//         headers: {
//             'Authorization': `Bearer ${accessToken}`,
//             'Content-Type': 'application/json',
//         },
//         body: body ? JSON.stringify(body) : null,
//     }).then(response => {

//         if (response.status === 200 || response.status === 201) {
//             return response.json().then(data => {
//                 // 중간 결과 출력 (디버깅용)
//                 console.log('Parsed Response Data:', data);

//                 // success 콜백 호출
//                 success(data);
//             });
//         }
//         const refreshToken = getCookie('refresh_token');  // 쿠키에서 리프레시 토큰 가져오기

//         if (response.status === 401 && refreshToken) {
//           // 액세스 토큰 만료 시 리프레시 토큰으로 새로운 액세스 토큰 요청
//             fetch('/api/token', {
//                 method: 'POST',
//                 headers: {
//                     'Authorization': `Bearer ${accessToken}`,
//                     'Content-Type': 'application/json',
//                 },
//                 body: JSON.stringify({
//                     refreshToken: refreshToken,
//                 }),
//             })
//                 .then(res => {
//                     if (res.ok) {
//                         return res.json();
//                     }
//                     throw new Error("Refresh token request failed");
//                 })
//                 .then(result => {
//                   // 새로운 액세스 토큰 저장
//                     localStorage.setItem('access_token', result.accessToken);
//                   // 새로 발급받은 액세스 토큰으로 재시도
//                     httpRequest(method, url, body, success, fail);
//                 })
//                 .catch(() => {
//                     fail();
//                 });
//         } else {
//           fail();  // 그 외 오류 처리
//         }
//     })
//     .catch(() => {
//       fail();  // 요청 실패 처리
//     });
// }
