package com.b.beep.domain.user.controller.dto.response

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.user.domain.UserRole
import com.b.beep.domain.user.entity.StudentInfoEntity
import com.b.beep.domain.user.entity.UserEntity

data class UserResponse(
    val id: Long? = null,
    val email: String,
    val username: String,
    val role: UserRole,
    val profileImage: String? = null,
    var studentInfo: StudentInfoResponse? = null,
    val currentStatus: AttendanceType
) {
    companion object {
        fun of(user: UserEntity, studentInfo: StudentInfoEntity? = null): UserResponse {
            return UserResponse(
                id = user.id,
                email = user.email,
                username = user.username,
                role = user.role,
                profileImage = user.profileImage,
                studentInfo = studentInfo?.let { StudentInfoResponse.of(it) },
                currentStatus = user.currentStatus
            )
        }
    }
}
