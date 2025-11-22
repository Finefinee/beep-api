package com.b.beep.domain.approval.controller.dto.request

import com.b.beep.domain.attendance.domain.enums.Room

data class ApproveRequest(
    val roomName: Room
)
