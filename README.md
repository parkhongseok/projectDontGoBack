# 프로젝트 돈고백(Dont go back)

## 목차

<pre>
1. 프로젝트 개요
2. 기술 스택
3. 아키텍처
</pre>

## 1. 프로젝트 개요

- 프로젝트 돈고백(Dont go back) - `투자 손익 기반 익명 SNS 서비스`
- 기간 : 2025.01.13 ~ (진행 중)
- 인원 : 개인 프로젝트
- 배포 : [https://dontgoback.kro.kr/](https://dontgoback.kro.kr/)
- 목표
  <pre>
  1.  Spring Security, OAuth2, JWT 기반 회원 인증
  2.  JPA와 Hibernate를 기반 ORM 기술 실습
  3.  라즈베리파이 홈서버를 구축 및 테스트 환경 구성
  4.  AWS 서비스를 포함한 배포 환경 구성
  5.  Docker, GitHub Action 사용한 빌드 및 배포 자동화
  </pre>

## 기술 스택

|      분류      |                                     도구                                      |  버전   |
| :------------: | :---------------------------------------------------------------------------: | :-----: |
|      언어      |                               Java / TypeScript                               | 21 / ^5 |
|    Frontend    | Next.js [[...]](./docs/architecture/decisions/01-프론트엔드-기술스택-선정.md) | 15.1.7  |
|    Backend     | Spring boot [[...]](./docs/architecture/decisions/02-백엔드-기술스택-선정.md) |  3.4.0  |
|       DB       |     MariaDB [[...]](./docs/architecture/decisions/03-DB-기술스택-선정.md)     | 10.11.6 |
|  Testing tool  |                                 Junit, Mockio                                 |         |
|     DevOps     |                             GitHub Action, Docker                             |         |
| Infrastructure |                          Raspberry Pi / AWS EC2, ECR                          |         |

</br>

## 아키텍처

아래 내용은 아키텍처 결정 레코드에 첨부한 이미지입니다. 전체 내용은 [여기](./docs/architecture/decisions/)를 참고 부탁드리겠니다.

- ### 빌드 및 배포 자동화

  [...자세히 보기](./docs/architecture/decisions/09-빌드-및-배포-자동화-프로세스.md)

  !["CI/CD Architecture"](./docs/architecture/decisions/09-빌드-및-배포-자동화-프로세스.png)

- ### 시스템 구조

  [...자세히 보기](./docs/architecture/decisions/04-시스템-아키텍처.md)

  !["System Architecture"](./docs/architecture/decisions/04-시스템-아키텍처.jpg)

- ### 회원 인증/인가 구조

  [...자세히 보기](./docs/architecture/decisions/08-OAuth2-JWT-인증-인가-흐름.md)

  !["OAuth2 Architecture"](./docs/architecture/decisions/08-OAuth2-JWT-인증-인가-흐름.png)

- ### 도메인 모델 설계

  [...자세히 보기](./docs/architecture/decisions/05-도메인-모델-설계.md)

  !["Domain Architecture"](./docs/architecture/decisions/05-도메인-모델-설계.jpg)

- ### 데이터 모델 설계

  [...자세히 보기](./docs/architecture/decisions/06-데이터-모델-및-ERD-설계.md)

  !["Data Architecture"](./docs/architecture/decisions/06-데이터-모델-및-ERD-설계.png)

- ### 엔티티 모델 설계

  [...자세히 보기](./docs/architecture/decisions/07-JPA-기반-엔티티-설계.md)

  !["Entity Architecture"](./docs/architecture/decisions/07-JPA-기반-엔티티-설계.jpg)
