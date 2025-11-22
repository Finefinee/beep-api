package com.b.beep.domain.user.entity

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import jakarta.persistence.*

@Entity
@Table(
    name = "fixed_rooms",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "type"])
    ]
)
class FixedRoomEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: UserEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: AttendanceType,

    @Enumerated(EnumType.STRING)
    @Column(name = "room", nullable = false)
    var room: Room
)
