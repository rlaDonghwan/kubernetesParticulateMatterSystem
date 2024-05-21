# 공식 OpenJDK 런타임 이미지를 부모 이미지로 사용합니다.
FROM openjdk:17-jdk-slim

# 작업 디렉터리를 설정합니다.
WORKDIR /app

# Gradle 래퍼 및 기타 Gradle 파일을 복사합니다.
COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle

# gradlew에 실행 권한을 부여합니다.
RUN chmod +x gradlew

# 의존성을 다운로드합니다.
RUN ./gradlew build -x test || return 0

# 프로젝트의 나머지 파일을 복사합니다.
COPY . .

# 애플리케이션을 빌드합니다.
RUN ./gradlew build -x test --no-daemon

# 최종 빌드를 위해 더 작은 JRE 이미지를 사용합니다.
FROM openjdk:17-jdk-slim

# 작업 디렉터리를 설정합니다.
WORKDIR /app

# 빌드 단계에서 빌드된 jar 파일을 복사합니다.
COPY --from=0 /app/build/libs/fineDust-0.0.1-SNAPSHOT.jar app.jar

# Spring Boot 애플리케이션이 실행되는 포트를 엽니다.
EXPOSE 8181

# jar 파일을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]
