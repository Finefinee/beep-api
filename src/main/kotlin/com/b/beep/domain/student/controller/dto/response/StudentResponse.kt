package com.b.beep.domain.student.controller.dto.response

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.student.controller.dto.StudentQueryDto
import com.b.beep.domain.user.controller.dto.response.FixedRoomResponse

data class StudentResponse(
    val username: String,
    val studentId: String,
    val fixedRooms: List<FixedRoomResponse>?,
    val statuses: List<StatusResponse>
) {
    companion object {
        fun of(
            studentQueryDto: StudentQueryDto
        ): StudentResponse {
            return StudentResponse(
                username = studentQueryDto.user.username,
                studentId = String.format("%d%d%02d", studentQueryDto.studentInfo.grade, studentQueryDto.studentInfo.cls, studentQueryDto.studentInfo.num),
                fixedRooms = studentQueryDto.fixedRooms.orEmpty().mapNotNull { room ->
                    room?.let { FixedRoomResponse.of(it) }
                },
                statuses = studentQueryDto.attendances.orEmpty().map { attendance ->
                    attendance.let { StatusResponse(it.period, it.type) }
                }
            )
        }

        private fun ofFixedRoomNull(
            studentQueryDto: StudentQueryDto
        ): StudentResponse {
            return StudentResponse(
                username = studentQueryDto.user.username,
                studentId = String.format("%d%d%02d", studentQueryDto.studentInfo.grade, studentQueryDto.studentInfo.cls, studentQueryDto.studentInfo.num),
                fixedRooms = null,
                statuses = studentQueryDto.attendances.orEmpty().map { attendance ->
                    attendance.let { StatusResponse(it.period, it.type) }
                }
            )
        }

        fun ofStudentQueryDtoList(
            studentQueryDtos: List<StudentQueryDto>
        ): List<StudentResponse> {
            return studentQueryDtos.map { dto ->
                of(dto)
            }
        }

        fun ofStudentQueryDtoListFixedRoomsNull(
            studentQueryDtos: List<StudentQueryDto>
        ): List<StudentResponse> {
            return studentQueryDtos.map { dto ->
                ofFixedRoomNull(dto)
            }
        }
    }
}

data class StatusResponse(
    val period: Int,
    val status: AttendanceType
)
