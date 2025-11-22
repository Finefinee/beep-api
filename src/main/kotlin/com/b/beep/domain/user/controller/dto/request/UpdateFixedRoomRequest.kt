package com.b.beep.domain.user.controller.dto.request

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room

data class UpdateFixedRoomRequest(
    val room: Room? = null,
    val type: AttendanceType? = null
)
