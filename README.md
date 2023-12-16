# OnLog_Auth_Server

## 🌐 프로젝트 개요

해당 프로젝트는 OnLog의 인증 프로세스를 처리하는 프로젝트입니다.

이 프로젝트는 주로 사용자의 카카오 로그인 및 로그아웃 기능을 관리하고 있습니다.

하지만 해당 프로젝트는 [OnLog Server](https://github.com/KEAPoint/OnLog_Post_Server)와 통합됨에 따라 현재 deprecated 처리되었습니다.

## 🛠️ 프로젝트 개발 환경

프로젝트는 아래 환경에서 개발되었습니다.

> OS: macOS Sonoma   
> IDE: Intellij IDEA  
> Java 17

## ✅ 프로젝트 실행

해당 프로젝트를 추가로 개발 혹은 실행시켜보고 싶으신 경우 아래의 절차에 따라 진행해주세요

#### 1. `secret.yml` 생성

```commandline
cd ./src/main/resources
touch secret.yml
```

#### 2. `secret.yml` 작성

```text
spring:
  datasource:
    url: {DATABASE_URL}
    username: {DATABASE_USERNAME}
    password: {DATABASE_PASSWORD}

  security:
    oauth2:
      client:
        registration:
          kakao:
            client-id: {KAKAO_CLIENT_ID}

jwt:
  secret-key: {SECRET_KEY}
```

#### 3. 프로젝트 실행

```commandline
./gradlew bootrun
```

**참고) 프로젝트가 실행 중인 환경에서 아래 URL을 통해 API 명세서를 확인할 수 있습니다**

```commandline
http://localhost:8080/swagger-ui/index.html
```