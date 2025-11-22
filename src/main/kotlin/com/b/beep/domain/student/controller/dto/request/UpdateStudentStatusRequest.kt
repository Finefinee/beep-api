package com.b.beep.domain.student.controller.dto.request

import com.b.beep.domain.attendance.domain.enums.AttendanceType

data class UpdateStudentStatusRequest(
    val grade: Int,
    val cls: Int,
    val num: Int,
    val status: AttendanceType,
)
