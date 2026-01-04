package com.b.beep.domain.student.controller.dto

import com.b.beep.domain.attendance.entity.AttendanceEntity
import com.b.beep.domain.user.entity.FixedRoomEntity
import com.b.beep.domain.user.entity.StudentInfoEntity
import com.b.beep.domain.user.entity.UserEntity
import com.querydsl.core.annotations.QueryProjection

data class StudentQueryDto @QueryProjection constructor(
    val user: UserEntity,
    val studentInfo: StudentInfoEntity,
    val fixedRooms: List<FixedRoomEntity?>?,
    val attendances: List<AttendanceEntity>?
)