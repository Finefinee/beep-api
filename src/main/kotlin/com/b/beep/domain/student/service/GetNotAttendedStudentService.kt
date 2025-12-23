package com.b.beep.domain.student.service

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import com.b.beep.domain.attendance.repository.AttendanceRepository
import com.b.beep.domain.student.controller.dto.response.StudentResponse
import com.b.beep.domain.student.controller.dto.response.StudentResponse.Companion.ofStudentQueryDtoList
import com.b.beep.domain.student.controller.dto.response.StudentResponse.Companion.ofStudentQueryDtoListFixedRoomsNull
import com.b.beep.domain.student.repository.StudentQueryRepository
import com.b.beep.domain.user.domain.UserRole
import com.b.beep.domain.user.repository.FixedRoomRepository
import com.b.beep.domain.user.repository.StudentInfoRepository
import com.b.beep.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetNotAttendedStudentService(
    private val userRepository: UserRepository,
    private val studentQueryRepository: StudentQueryRepository,
    private val studentInfoRepository: StudentInfoRepository,
    private val fixedRoomRepository: FixedRoomRepository,
    private val attendanceRepository: AttendanceRepository
) {
    fun getAll(type: AttendanceType): List<StudentResponse> {
        val studentQueryDtos = studentQueryRepository.findAllInfoByRoomTypeStatusAndRole(type, AttendanceType.NOT_ATTEND, UserRole.STUDENT)
        return ofStudentQueryDtoList(studentQueryDtos)
    }

    fun getAllByRoomAndType(room: Room, type: AttendanceType): List<StudentResponse> {
        val studentQueryDtos = studentQueryRepository.findAllInfoByStatusAndRoomAndType(room, type, AttendanceType.NOT_ATTEND)
        return ofStudentQueryDtoList(studentQueryDtos)
    }

    fun getAllByGradeAndCls(grade: Int, cls: Int): List<StudentResponse> {
        val studentQueryDtos = studentQueryRepository.findAllInfoByStatusAndGradeAndCls(grade, cls)
        return ofStudentQueryDtoListFixedRoomsNull(studentQueryDtos)
    }
}
