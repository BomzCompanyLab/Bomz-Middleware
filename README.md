# bomz-middleware

**BOMZ Public Project Middleware 1.0**

자바 기반의 RFID/USN Middleware

대량의 RFID/USN 데이터를 수신 후 오류제거/가공/취합/그룹핑하여 필요한 데이터를 원하는 형태 및 주기로 최종 응용프로그램에게 전달하는 역활 수행

**논리그룹**
   - 장치와 논리그룹간의 1:1 , 1:N, N:1, N:N 관계 설정으로 데이터 처리

**리포터**
   - 논리그룹과 리포터간의 1:1, 1:N, N:1, N:N 관계 설정으로 데이터 처리

**시스템관리**
   - JavaFX 기반의 관리툴 기본 제공
   - SOAP 기반의 OpenAPI 제공

**사용자 맞춤 개발**
   - RFID/USN 장치별로 다른 프로토콜을 맞추기 위해 기본 모듈을 기반으로 장치 수신 드라이버 개발
   - 가공된 데이터 전송을 위해 요청에 맞는 프로토콜(TCP/HTTP/ETC...) 에 따라 맞춤 리포터 드라이버 개발




MIT License
Copyright (c) 2021 BOMZ
