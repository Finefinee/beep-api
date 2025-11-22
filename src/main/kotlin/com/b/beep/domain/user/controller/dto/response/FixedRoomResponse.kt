package com.b.beep.domain.user.controller.dto.response

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import com.b.beep.domain.user.entity.FixedRoomEntity

data class FixedRoomResponse(
    val id: Long,
    val room: Room,
    val type: AttendanceType
) {
    companion object {
        fun of(fixedRoom: FixedRoomEntity): FixedRoomResponse {
            return FixedRoomResponse(
                id = fixedRoom.id!!,
                room = fixedRoom.room,
                type = fixedRoom.type
            )
        }
    }
}
