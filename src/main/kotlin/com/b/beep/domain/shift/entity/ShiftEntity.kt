package com.b.beep.domain.shift.entity

import com.b.beep.domain.user.entity.UserEntity
import com.b.beep.global.common.entity.BaseEntity
import com.b.beep.domain.attendance.domain.enums.Room
import com.b.beep.domain.shift.domain.enums.ShiftStatus
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(
    name = "shifts",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "date", "period"])
    ]
)
class ShiftEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "room", nullable = false)
    var room: Room,

    @Column(name = "period", nullable = false)
    var period: Int,

    var reason: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ShiftStatus,

    var date: LocalDate,
) : BaseEntity()