package com.etelie.securities

data class SecurityTerm(
    val length: Int,
    val unit: Unit,
) {

    companion object {
        fun months(length: Int): SecurityTerm = SecurityTerm(length, Unit.MONTHS)
        fun weeks(length: Int): SecurityTerm = SecurityTerm(length, Unit.WEEKS)
    }

    enum class Unit {
        WEEKS, MONTHS;
    }

}
