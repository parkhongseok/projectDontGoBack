// 쿠키에서 값 가져오기 함수
export function getCookie(name: string) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(';').shift();
    return undefined;
    }

// HTTP 요청 보내는 함수
export function httpRequest(
    method: string,
    url: string,
    body: any,
    success: (result: any) => void,
    fail: () => void
) {
    // 로컬 스토리지에서 액세스 토큰 가져오기
    const accessToken = localStorage.getItem('access_token');
        
    fetch(url, {
        method: method,
        credentials: 'include',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json',
        },
        body: body ? JSON.stringify(body) : null,
    }).then(response => {
        
        if (response.status === 200 || response.status === 201) {
            return response.json().then(data => {
                // 중간 결과 출력 (디버깅용)
                console.log('Parsed Response Data:', data);

                // success 콜백 호출
                success(data);
            });
        }

        const refreshToken = getCookie('refresh_token');  // 쿠키에서 리프레시 토큰 가져오기

        if (response.status === 401 && refreshToken) {
          // 액세스 토큰 만료 시 리프레시 토큰으로 새로운 액세스 토큰 요청
            fetch('/api/token', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${accessToken}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    refreshToken: refreshToken,
                }),
            })
                .then(res => {
                    if (res.ok) {
                        return res.json();
                    }
                    throw new Error("Refresh token request failed");
                })
                .then(result => {
                  // 새로운 액세스 토큰 저장
                    localStorage.setItem('access_token', result.accessToken);
                  // 새로 발급받은 액세스 토큰으로 재시도
                    httpRequest(method, url, body, success, fail);
                })
                .catch(() => {
                    fail();
                });
        } else {
          fail();  // 그 외 오류 처리
        }
    })
    .catch(() => {
      fail();  // 요청 실패 처리
    });
}
