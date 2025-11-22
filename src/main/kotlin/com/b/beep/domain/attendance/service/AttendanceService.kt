package com.b.beep.domain.attendance.service

import com.b.beep.domain.attendance.controller.dto.request.AttendRequest
import com.b.beep.domain.attendance.domain.PeriodResolver
import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.error.AttendanceError
import com.b.beep.domain.attendance.repository.AttendanceRepository
import com.b.beep.domain.user.repository.FixedRoomRepository
import com.b.beep.domain.user.repository.UserRepository
import com.b.beep.global.exception.CustomException
import com.b.beep.global.security.ContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class AttendanceService(
    private val attendanceRepository: AttendanceRepository,
    private val contextHolder: ContextHolder,
    private val fixedRoomRepository: FixedRoomRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun attend(request: AttendRequest) {
        val user = contextHolder.user
        val period = PeriodResolver.getCurrentAttendancePeriod()

        // 고정실 검증
        val fixedRoom = fixedRoomRepository.findByUserAndType(user, request.attendanceType)
            ?: throw CustomException(AttendanceError.NOT_EXISTS_ATTEND_TYPE)

        if (request.room != fixedRoom.room) {
            throw CustomException(AttendanceError.ROOM_MISMATCH)
        }

        val attendance = attendanceRepository.findByUserIdAndPeriodAndDate(user.id!!, period, LocalDate.now())
            ?: throw CustomException(AttendanceError.ATTENDANCE_NOT_FOUND)

        if (attendance.type != AttendanceType.NOT_ATTEND) {
            throw CustomException(AttendanceError.ALREADY_ATTENDED)
        }

        attendance.type = request.attendanceType
        attendance.room = request.room
        user.currentStatus = request.attendanceType

        attendanceRepository.save(attendance)

        // 8교시 출석 시 9교시도 자동 출석 처리
        if (period == 8) {
            val nextPeriodAttendance = attendanceRepository.findByUserIdAndPeriodAndDate(user.id!!, 9, LocalDate.now())
            if (nextPeriodAttendance != null && nextPeriodAttendance.type == AttendanceType.NOT_ATTEND) {
                nextPeriodAttendance.type = request.attendanceType
                nextPeriodAttendance.room = request.room
                attendanceRepository.save(nextPeriodAttendance)
            }
        }

        userRepository.save(user)
    }

    @Transactional
    fun cancelAttendance() {
        val user = contextHolder.user
        val period = PeriodResolver.getCurrentAttendancePeriod()

        val attendance = attendanceRepository.findByUserIdAndPeriodAndDate(user.id!!, period, LocalDate.now())
            ?: throw CustomException(AttendanceError.ATTENDANCE_NOT_FOUND)

        attendance.type = AttendanceType.NOT_ATTEND
        attendance.room = null
        user.currentStatus = AttendanceType.NOT_ATTEND

        attendanceRepository.save(attendance)
        userRepository.save(user)
    }
}
