package com.b.beep.domain.shift.service

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.error.AttendanceError
import com.b.beep.domain.attendance.repository.AttendanceRepository
import com.b.beep.domain.shift.entity.ShiftEntity
import com.b.beep.domain.shift.repository.ShiftRepository
import com.b.beep.domain.shift.domain.enums.ShiftStatus
import com.b.beep.domain.shift.domain.error.ShiftError
import com.b.beep.global.exception.CustomException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class ShiftManagementService(
    private val shiftRepository: ShiftRepository,
    private val attendanceRepository: AttendanceRepository,
) {
    fun getAll(): List<ShiftEntity> {
        return shiftRepository.findAllByDateGreaterThanEqual(LocalDate.now())
    }

    @Transactional
    fun updateStatus(id: Long, status: ShiftStatus) {
        val shift = shiftRepository.findByIdOrNull(id)
            ?: throw CustomException(ShiftError.SHIFT_NOT_FOUND)

        shift.status = status

        if (status == ShiftStatus.APPROVED && shift.date == LocalDate.now()) {
            val attendance = attendanceRepository
                .findByPeriodAndUserAndDate(shift.period, shift.user, LocalDate.now())
                ?: throw CustomException(AttendanceError.ATTENDANCE_NOT_FOUND)

            attendance.type = AttendanceType.SHIFT_ATTEND
            attendance.room = shift.room

            attendanceRepository.save(attendance)
        }

        shiftRepository.save(shift)
    }
}
