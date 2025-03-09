# 1단계: 빌드 환경 설정
FROM openjdk:21-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 파일 복사 (빌드를 위한 파일들)
COPY . .

# 프로젝트 빌드 (예: Gradle을 사용하여 JAR 파일 빌드)
RUN ./gradlew build

# 포트 노출
EXPOSE 8090

# 2단계: 실행 환경 설정
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드한 JAR 파일 복사 (경로를 프로젝트명에 맞게 수정)
COPY --from=build /app/build/libs/dontgo-0.0.1-SNAPSHOT.jar app.jar

# 포트 노출
EXPOSE 8090

# 서버 실행
CMD ["sh", "-c", "sleep 10 && java -jar app.jar"]
# CMD ["java", "-jar", "app.jar"]