- [구글 스프레드 시트 API 문서](https://developers.google.com/sheets/api/guides/concepts?hl=ko)
- [구글 스프레드 시트 Quick Start 문서](https://developers.google.com/sheets/api/quickstart/java?hl=ko)

스프레드 시트
- 스프레드 시트 리소스는 모든 스프레드 시트를 나타냄
- 문자, 숫자, 하이픈, 밑줄이 포함된 고유한 spreadsheetId 값

ex) `https://docs.google.com/spreadsheets/d/spreadsheetId/edit#gid=0`

시트
- 스프레드 시트 내의 각 시트 (탭) 을 나타냄
- 제목과 숫자로 구성된 sheetId를 가짐 

ex) `https://docs.google.com/spreadsheets/d/aBC-123_xYz/edit#gid=sheetId`

## TODO List

* [x] 구글 시트에서 데이터 가져오기
* [x] 구글 시트에 데이터 업데이트
* [x] 구글 시트 리소스에 접근할 때 사용자 인증 화면 없이 접근 가능하도록 변경
* [x] 네이버 쇼핑몰에서 새로 추가된 주문을 구글 시트에 등록
  * [x] 네이버 쇼핑몰 API에서 최근 상태 변경된 주문 목록을 가져올 수 있다.
  * [x] 네이버 상태 변경된 주문 목록 중 `결제됨(PAYED)` 상태인 주문만 가져온다.
  * [x] 구글 시트에 등록된 네이버 주문 목록을 가져온다.
  * [x] 구글 시트에 등록된 네이버 주문 목록 중 최신 상태가 `결제됨` 상태인 주문만 가져온다.
  * [x] 구글 시트에 신규 주문 목록 + 이미 등록된 주문 목록에서 배송 전 상태인 주문으로 업데이트 한다.
  * [x] 네이버 신규 주문은 Line 메시지로 알림을 보낼 수 있다.
  * [x] 네이버 신규 주문은 발주 확인 상태로 업데이트 한다.
* [ ] 취소 요청된 주문에 대해 알림을 받을 수 있다.
* [ ] 발송 기간이 얼마 남지 않은 상품에 대해 알림을 받을 수 있다.
  * [ ] 발송 기한 1일 전이면 알림을 보낸다.

---
