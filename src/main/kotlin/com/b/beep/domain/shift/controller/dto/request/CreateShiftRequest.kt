package com.b.beep.domain.shift.controller.dto.request

import com.b.beep.domain.attendance.domain.enums.Room
import java.time.LocalDate

data class CreateShiftRequest(
    val room: Room,
    val reason: String,
    val period: Int,
    val date: LocalDate
)
