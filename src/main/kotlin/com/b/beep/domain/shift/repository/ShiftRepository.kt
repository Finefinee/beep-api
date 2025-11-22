package com.b.beep.domain.shift.repository

import com.b.beep.domain.shift.entity.ShiftEntity
import com.b.beep.domain.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ShiftRepository : JpaRepository<ShiftEntity, Long> {
    fun findAllByUser(user: UserEntity): List<ShiftEntity>
    fun existsByUserAndDateAndPeriod(user: UserEntity, date: LocalDate, period: Int): Boolean
    fun findAllByDateGreaterThanEqual(date: LocalDate): List<ShiftEntity>
    fun deleteByDateBefore(date: LocalDate)
}