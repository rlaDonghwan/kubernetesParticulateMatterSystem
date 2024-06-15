FROM openjdk:17-jdk-slim

# /tmp 디렉토리에 대한 볼륨 추가
VOLUME /tmp

# 이 컨테이너 외부에서 8181 포트를 사용 가능하도록 설정
EXPOSE 8181

# curl 및 필요한 도구들 설치
RUN apt-get update && apt-get install -y curl net-tools

# 애플리케이션의 jar 파일
ARG JAR_FILE=build/libs/kubernetesParticulateMatterSystem-0.0.1-SNAPSHOT.jar

# 애플리케이션의 jar 파일을 컨테이너에 추가
ADD ${JAR_FILE} app.jar

# jar 파일 실행
ENTRYPOINT ["java","-jar","/app.jar"]
