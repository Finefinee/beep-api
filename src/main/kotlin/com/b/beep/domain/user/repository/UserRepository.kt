package com.b.beep.domain.user.repository

import com.b.beep.domain.user.entity.UserEntity
import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.user.domain.UserRole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun existsByEmail(email: String): Boolean
    fun findAllByCurrentStatus(currentStatus: AttendanceType): List<UserEntity>
    fun findAllByCurrentStatusAndRole(currentStatus: AttendanceType, role: UserRole): List<UserEntity>
    fun findAllByRole(role: UserRole): List<UserEntity>

    @Modifying
    @Query("UPDATE UserEntity u SET u.currentStatus = :status")
    fun updateAllCurrentStatus(@Param("status") status: AttendanceType)

    @Modifying
    @Query("UPDATE UserEntity u SET u.currentStatus = :status WHERE u IN :users")
    fun updateStatusByUsers(@Param("users") users: List<UserEntity>, @Param("status") status: AttendanceType)
}
