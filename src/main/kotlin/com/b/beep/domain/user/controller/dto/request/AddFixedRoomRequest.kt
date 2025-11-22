package com.b.beep.domain.user.controller.dto.request

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room

data class AddFixedRoomRequest(
    val room: Room,
    val type: AttendanceType
)