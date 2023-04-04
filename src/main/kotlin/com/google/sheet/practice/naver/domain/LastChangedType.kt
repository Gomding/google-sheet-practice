package com.google.sheet.practice.naver.domain

enum class LastChangedType(
    val description: String,
    val isGift: Boolean,
) {
    PAY_WAITING("결제 대기", false),
    PAYED("결제 완료", false),
    EXCHANGE_OPTION("옵션 변경", true),
    DELIVERY_ADDRESS_CHANGED("배송지 변경", false),
    GIFT_RECEIVED("선물 수락", true),
    CLAIM_REJECTED("클레임 철회", false),
    DISPATCHED("발송 처리", false),
    CLAIM_REQUESTED("클레임 요청", false),
    COLLECT_DONE("수거 완료", false),
    CLAIM_HOLDBACK_RELEASED("클레임 보류 해제", false),
    CLAIM_COMPLETED("클레임 완료", false),
    PURCHASE_DECIDED("구매 확정", false),
    HOPE_DELIVERY_INFO_CHANGED("배송 희망일 변경", false);

    companion object {
        fun of(name: String): LastChangedType {
            return valueOf(name)
        }
    }
}