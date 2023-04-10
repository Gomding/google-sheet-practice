[구글 스프레드 시트 API 문서](https://developers.google.com/sheets/api/guides/concepts?hl=ko)
[구글 스프렏드 시트 Quick Start 문서](https://developers.google.com/sheets/api/quickstart/java?hl=ko)

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

---

```json
{
  "timestamp":"2023-04-03T23:45:45.774+09:00", 
  "data":{
    "lastChangeStatuses":[
      {
        "receiverAddressChanged":false, 
        "productOrderId":2023032838302801, 
        "orderId":2023032816902831, 
        "productOrderStatus":"PURCHASE_DECIDED", 
        "paymentDate":"2023-03-28T21:04:34.0+09:00", 
        "lastChangedDate":"2023-04-03T20:02:23.0+09:00", 
        "lastChangedType":"PURCHASE_DECIDED"
      }
    ], 
    "count":1
  }, 
  "traceId":"ar2-230322-1zf9lf^1679464710739^36781341"
}
```

```json
{
  "timestamp":"2023-04-04T00:48:23.405+09:00", 
  "data":[
    {
      "productOrder":{
        "quantity":10, 
        "productOrderId":2023032838302801, 
        "mallId":"ncp_1nsut9_01", 
        "productClass":"조합형옵션상품", 
        "productOrderStatus":"PURCHASE_DECIDED", 
        "productName":"OOOOOOOOOO", 
        "productId":5271020390, 
        "itemNo":27477136223, 
        "placeOrderStatus":"OK", 
        "optionPrice":3900, 
        "productOption":"OOOOO", 
        "unitPrice":5140, 
        "productDiscountAmount":0, 
        "deliveryPolicyType":"유료", 
        "deliveryFeeAmount":10000, 
        "sectionDeliveryFee":0, 
        "totalPaymentAmount":90400, 
        "packageNumber":2023032862461806, 
        "shippingFeeType":"선결제", 
        "decisionDate":"2023-04-03T20:02:22.0+09:00", 
        "shippingDueDate":"2023-03-31T23:59:59.0+09:00", 
        "deliveryDiscountAmount":0, 
        "optionCode":27477136223, 
        "placeOrderDate":"2023-03-29T01:36:52.0+09:00", 
        "shippingAddress":{
          "zipCode":"05359", 
          "baseAddress":"서울특별시 강동구 천호대로 ~~" , 
          "addressType":"DOMESTIC", 
          "detailedAddress":"x동 xxxx호", 
          "tel1":"010-1234-1234", 
          "isRoadNameAddress":true, 
          "name":"OOO"
        }, 
        "totalProductAmount":90400, 
        "sellerBurdenDiscountAmount":0, 
        "saleCommission":0, 
        "expectedDeliveryMethod":"DELIVERY", 
        "takingAddress":{
          "zipCode":"01234", 
          "city":"city", 
          "baseAddress":"1234", 
          "addressType":"FOREIGN", 
          "detailedAddress":"asdf", 
          "tel1":"000-0000-0000", 
          "tel2":"000-0000-0000", 
          "isRoadNameAddress":false, 
          "name":"OOOOO", 
          "state":"asdf", 
          "country":"country"
        }, 
        "commissionRatingType":"결제수수료", 
        "commissionPrePayStatus":"GENERAL_PRD", 
        "paymentCommission":1789, 
        "expectedSettlementAmount":86803, 
        "inflowPath":"검색>쇼핑검색(네이버쇼핑)", 
        "channelCommission":0, 
        "knowledgeShoppingSellingInterlockCommission":1808
      },
      "delivery":{
        "deliveryMethod":"DELIVERY", 
        "deliveryCompany":"EMS", 
        "sendDate":"2023-03-29T17:39:32.976+09:00", 
        "trackingNumber":"aaaaaaaaaa", 
        "pickupDate":"2023-03-29T15:19:00.0+09:00", 
        "deliveryStatus":"DELIVERY_COMPLETION", 
        "deliveredDate":"2023-04-03T09:22:00.0+09:00", 
        "isWrongTrackingNumber":false
      }, 
      "order":{
        "ordererTel":"01012345678", 
        "ordererNo":1000000, 
        "payLocationType":"MOBILE", 
        "orderId":1111111111111, 
        "paymentDate":"2023-03-28T21:04:34.0+09:00", 
        "orderDiscountAmount":0, 
        "orderDate":"2023-03-28T21:04:26.0+09:00", 
        "chargeAmountPaymentAmount":0, 
        "generalPaymentAmount":100400, 
        "naverMileagePaymentAmount":0, 
        "ordererId":"asdf****", 
        "ordererName":"OOO", 
        "paymentMeans":"신용카드 간편결제", 
        "isDeliveryMemoParticularInput":false, 
        "payLaterPaymentAmount":0
      }
    }
  ], 
  "traceId":"ar2-230322-1kznwp^1679464709759^36890367"
}
```