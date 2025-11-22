package com.b.beep.domain.attendance.domain

import com.b.beep.domain.attendance.domain.error.AttendanceError
import com.b.beep.global.exception.CustomException
import java.time.LocalTime
import java.time.ZoneId

object PeriodResolver {
    fun getCurrentAttendancePeriod(): Int {
        val now = LocalTime.now(ZoneId.of("Asia/Seoul"))

        return if (now.isAfter(LocalTime.of(16, 25)) && now.isBefore(LocalTime.of(16, 40))) 8
        else if (now.isAfter(LocalTime.of(19, 10)) && now.isBefore(LocalTime.of(19, 30))) 10
        else if (now.isAfter(LocalTime.of(20, 40)) && now.isBefore(LocalTime.of(20, 59))) 11
        else throw CustomException(AttendanceError.TIME_UNAVAILABLE)
    }

    fun getCurrentPeriod(): Int {
        val now = LocalTime.now(ZoneId.of("Asia/Seoul"))

        return if (now.isAfter(LocalTime.of(16, 25)) && now.isBefore(LocalTime.of(19, 9, 59))) 8
        else if (now.isAfter(LocalTime.of(19, 10)) && now.isBefore(LocalTime.of(20, 39, 59))) 10
        else if (now.isAfter(LocalTime.of(20, 40)) && now.isBefore(LocalTime.of(21, 40))) 11
        else 0
    }
}
