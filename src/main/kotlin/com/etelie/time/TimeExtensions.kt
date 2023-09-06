package com.etelie.time

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaZoneId

fun TimeZone.toJavaTimeZone(): java.util.TimeZone {
    return java.util.TimeZone.getTimeZone(this.toJavaZoneId())
}
