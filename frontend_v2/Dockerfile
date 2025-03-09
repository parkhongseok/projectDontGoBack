# 1단계: Node.js 기반 이미지로 시작
FROM node:20-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 패키지.json과 package-lock.json을 먼저 복사해서 의존성 설치
COPY package*.json ./
RUN npm install

# Next.js 빌드
COPY . .
RUN npm run build

# 2단계: 최종 이미지 설정 (슬림한 이미지를 사용)
FROM node:20-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물만 복사
COPY --from=builder /app ./

# 3단계: 앱 실행
CMD ["npm", "start"]
