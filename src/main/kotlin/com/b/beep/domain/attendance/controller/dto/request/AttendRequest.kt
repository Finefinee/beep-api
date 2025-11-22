package com.b.beep.domain.attendance.controller.dto.request

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room

data class AttendRequest(
    val room: Room,
    val attendanceType: AttendanceType
)
