# 2주차
---
## REST API

REST API는 API의 한 종류로 웹에서 HTTP를 표준적으로 쓰는 방식이다.

리소스 중심이며 HTTP 메서드/상태코드/캐시 등을 표준적으로 활용한다. 

CRUD와 공개 웹 API에 적합하다.


### REST의 6가지 제약

1. 클라이언트-서버 분리: UI와 데이터/비즈니스 로직 분리 → 독립적 개발 및 배포

2. 무상태(Stateless): 서버는 요청 간 클라이언트 상태를 저장하지 않음 → 확장성↑ (세션 대신 토큰/JWT 등)

3. 캐시 가능(Cacheable): HTTP 캐시 헤더로 네트워크 비용↓, 지연↓.

4. 균일한 인터페이스(Uniform Interface)

    리소스는 URI로 식별

    리소스의 표현(Representation) 을 전송(JSON 등)

    표준 메서드 의미 준수(GET/POST/PUT/PATCH/DELETE)

    (선택) 하이퍼미디어(HATEOAS)

5. 계층화(Layered System): 로드밸런서, 프록시, 게이트웨이 등 중간 계층 허용

6. 코드 온 디맨드(선택): 필요 시 스크립트 내려받아 실행

   
### 리소스와 URI 설계 원칙

명사형, 복수형: /users, /orders/{id}, /boards/{boardId}/comments

행위는 메서드로: /users/123/activate(X) → PATCH /users/123 { "active": true }(O)

일관성: 소문자, 하이픈 또는 밑줄 중 하나로 통일

필터/정렬/페이징: GET /boards?autonomousDistrict=노원구&sort=-createdAt&page=2&size=20

컬렉션 vs 개별: /items(컬렉션), /items/{id}(개별)


### 예시(게시판)

엔드포인트 설계

GET /boards (목록, 필터/정렬/페이징)

POST /boards (작성)

GET /boards/{id} (상세)

PATCH /boards/{id} (부분 수정)

DELETE /boards/{id} (삭제)

---

## HTTP

HTTP(HyperText Transfer Protocol)는 요청–응답(Request–Response) 모델의 애플리케이션 레벨 프로토콜이다.

클라이언트(브라우저/앱)가 요청(request) 을 보내면, 서버가 응답(response) 으로 상태 코드·헤더·바디를 돌려준다.

전송 자체는 TCP/TLS/QUIC 같은 하위(전송) 계층 위에서 수행된다.


### 메시지 구조(HTTP/1.1 관점)

#### 요청(Request)
```
GET /boards?page=2&size=20 HTTP/1.1
Host: api.example.com
Accept: application/json
Authorization: Bearer <JWT>
If-None-Match: "W/\"v5\""

(보통 GET은 바디 없음)
```

#### 응답(Response)
```
HTTP/1.1 200 OK
Content-Type: application/json
Cache-Control: max-age=60
ETag: "W/\"v5\""

{ "items": [ ... ], "pageInfo": { "page": 2, "size": 20 } }
```

### 상태 코드

2xx 성공: 200 OK, 201 Created(Location 헤더와 함께), 204 No Content

3xx 리다이렉트: 301/302/307/308, 304 Not Modified(조건부/캐시 히트)

4xx 클라이언트 오류: 400(잘못된 요청), 401(인증 필요), 403(권한 없음), 404(없음), 409(충돌), 422(검증 실패), 429(요청 과다)

5xx 서버 오류: 500, 502, 503, 504 등


### 연결 관리 & HTTP 버전

#### HTTP/1.1

기본 Keep-Alive(지속 연결), Chunked 전송(스트리밍/사이즈 미정 콘텐츠)

파이프라이닝 이론상 가능하지만 실무에선 비권장(헤드 오브 라인 블로킹)

#### HTTP/2 (h2)

바이너리 프레이밍, 멀티플렉싱(한 TCP 연결에서 여러 스트림 동시 처리)

HPACK 헤더 압축 → 지연/오버헤드 감소

#### HTTP/3 (h3)

QUIC(UDP 기반) 위에서 동작, 스트림 단위 재전송으로 지연·HOLB 완화

모바일/불안정 네트워크에서 성능 개선

TLS: HTTPS = HTTP + TLS. HSTS(Strict-Transport-Security)로 HTTPS 강제 가능

---

### 브라우저에URL을입력후요청하여서버에서응답하는과정

#### 1) URL 해석 & 사전 체크

브라우저가 URL을 스킴/호스트/경로/쿼리로 파싱 (https://example.com/path?a=1)

HSTS 프리로드 또는 이전 방문 기록으로 HTTP→HTTPS 업그레이드

Service Worker 또는 브라우저 캐시(ETag/Last-Modified)에서 캐시 히트 확인

#### 2) DNS 조회

순서: 브라우저/OS 캐시 → hosts → 로컬 리졸버 → (루트 → TLD → 권한 DNS)

CDN 사용 시 CNAME을 따라 엣지 서버 IP 획득 (DoH/DoT 가능)

#### 3) 연결 수립 (전송 계층)

HTTP/1.1/2: TCP 3-way 핸드셰이크

HTTP/3: QUIC(UDP) 기반으로 지연 감소, 스트림 단위 재전송

프록시/방화벽/로드밸런서 경유 가능

#### 4) TLS 핸드셰이크(HTTPS)

SNI로 가상호스트 선택, ALPN으로 h2/h3 결정

서버 인증서 체인 검증(루트 신뢰), OCSP 스테이플링 가능

키 합의 후 암호화 채널 성립

#### 5) HTTP 요청 전송

예: GET /path HTTP/2 + Host, Accept, Accept-Encoding, Cookie/Authorization 등 헤더

조건부 요청: If-None-Match(ETag) / If-Modified-Since

#### 6) 서버 측 처리

리버스 프록시/게이트웨이(Nginx) 에서 TLS 종료·라우팅·압축·캐시

WAF/인증/레이트리밋 → 애플리케이션 서버(예: Spring) → DB/캐시 조회 → 응답 생성

정적 자산은 CDN/엣지에서 바로 응답할 수 있음

#### 7) HTTP 응답

상태코드: 200/201/204/304/302/404/500 등

헤더: Content-Type, Cache-Control, ETag, Content-Encoding(gzip/br) 등

본문은 HTML/JSON/이미지 큰 응답은 chunked 또는 Range(206)

#### 8) 브라우저 렌더링 파이프라인

HTML 파싱 → DOM 생성, CSS 파싱 → CSSOM → 렌더 트리 → 레이아웃/페인트/컴포지팅

프리로드 스캐너가 CSS/JS/이미지 추가 요청, HTTP/2 멀티플렉싱으로 동시 다운로드

JS 실행, 이벤트 루프 동작, SPA 라우팅, 폰트 로딩, 웹폰트 FOIT/FOUT 대응

SOP/CORS 정책으로 교차 출처 리소스 제어

#### 9) 캐시·보안·성능 요약

캐시: Cache-Control, ETag + 304, 정적 파일은 해시 파일명 + 장기 캐시

보안: HTTPS/HSTS, CSP/SRI, 쿠키 HttpOnly/Secure/SameSite

성능: HTTP/2 멀티플렉싱, HTTP/3(QUIC), 압축(gzip/br), 이미지 포맷(WebP/AVIF), CDN

---
