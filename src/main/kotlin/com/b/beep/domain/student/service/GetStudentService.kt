package com.b.beep.domain.student.service

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import com.b.beep.domain.attendance.repository.AttendanceRepository
import com.b.beep.domain.student.controller.dto.response.StudentResponse
import com.b.beep.domain.student.controller.dto.response.StudentResponse.Companion.ofStudentQueryDtoList
import com.b.beep.domain.student.controller.dto.response.StudentResponse.Companion.ofStudentQueryDtoListFixedRoomsNull
import com.b.beep.domain.student.repository.StudentQueryRepository
import com.b.beep.domain.user.repository.FixedRoomRepository
import com.b.beep.domain.user.repository.StudentInfoRepository
import com.b.beep.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetStudentService(
    private val userRepository: UserRepository,
    private val studentQueryRepository: StudentQueryRepository,
    private val studentInfoRepository: StudentInfoRepository,
    private val fixedRoomRepository: FixedRoomRepository,
    private val attendanceRepository: AttendanceRepository
) {
    fun getAllByRoomAndType(room: Room, type: AttendanceType): List<StudentResponse> {
        val studentQueryDtos = studentQueryRepository.findAllInfoByStatusAndRoomAndType(room, type)
        return ofStudentQueryDtoList(studentQueryDtos)
    }

    fun getAllByGradeAndCls(grade: Int, cls: Int): List<StudentResponse> {
        val studentQueryDtos = studentQueryRepository.findAllInfoByStatusAndGradeAndCls(grade, cls)
        return ofStudentQueryDtoListFixedRoomsNull(studentQueryDtos)
    }

    // fixedRooms가 null이여야 한다면
    // ofStudentQueryDtoListFixedRoomsNull를 사용해보자
}
