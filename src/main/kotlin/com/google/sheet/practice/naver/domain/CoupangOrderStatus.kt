package com.google.sheet.practice.naver.domain

enum class CoupangOrderStatus(
    val description: String,
) {
    ACCEPT("결제완료"),
    INSTRUCT("상품준비중"),
    DEPARTURE("배송지시"),
    DELIVERING("배송중"),
    FINAL_DELIVERY("배송완료"),
    NONE_TRACKING("업체 직접 배송(배송 연동 미적용), 추적불가")
}