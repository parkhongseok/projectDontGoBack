# 1단계: 빌드 환경 설정
FROM openjdk:21-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사 (빌드를 위한 파일들)
COPY . .

# 프로젝트 빌드 (예: Gradle을 사용하여 JAR 파일 빌드)
RUN chmod +x gradlew
RUN ./gradlew clean bootJar -x test --no-daemon

# 포트 노출
EXPOSE 8090

# 2단계: 실행 환경 설정
FROM openjdk:21-jdk-slim

# ---------- Run stage ----------
# 타임존 설정 추가 (slim에는 시간 관련 table이 빠져있어서, 계산 불가하기에 tzdata 설치 요망)
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# 작업 디렉토리 설정
WORKDIR /app

# 빌드한 JAR 파일 복사
COPY --from=build /app/build/libs/dontgo-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8090
# JVM/타임존/메모리 기본값 예시 (선택)
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul -XX:+UseContainerSupport"
ENTRYPOINT ["java","-jar","/app/app.jar"]