# 내부 API 보안 강화를 위한 허용 IP 기반 필터 도입

Date: 2025-08-19  
Status: Accepted

## 맥락

테스트 또는 관리용으로 제공되는 내부 API(`/test/internal/**`)는
외부에서는 절대 접근이 불가능해야 하며, 로컬 또는 EC2 내부에서만 접근이 가능해야 한다.

기존에는 `127.0.0.1` 기반 IP 필터링으로 구현되어 있었으나 다음과 같은 문제가 있었다:

- EC2 내에서 `curl localhost` 요청을 보냈음에도 요청이 `127.0.0.1`이 아닌 Docker 브릿지 IP(`172.x.x.x`)로 전달됨
- `request.getRemoteAddr()`가 예상과 다르게 동작하여 필터에서 차단됨

## 결정

- `application.yml` 또는 `.env`에 `ALLOWED_INTERNAL_IPS` 환경변수를 정의하고, 이를 통해 허용 IP를 외부에서 주입받도록 개선
- 필터에서는 `custom.allowed-ips` 값을 파싱하여 허용된 IP Set을 생성
- `request.getRemoteAddr()` 기준으로 검사하되, 사전에 실제 IP를 로그로 확인하여 등록함
- 이 방식은 Docker, EC2, 테스트 환경을 모두 포괄할 수 있음

## 결과

- 외부 요청에 대한 내부 API 접근 완전 차단
- 운영 배포 중에도 필요 시 안전하게 테스트 배치를 트리거할 수 있는 안전한 수단 확보
- IP 추가가 필요할 경우, `.env` 값만 수정하고 서버 재시작으로 반영 가능
