package com.b.beep.domain.notification.repository

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.notification.entity.FcmTokenEntity
import com.b.beep.domain.notification.entity.QFcmTokenEntity
import com.b.beep.domain.user.entity.QUserEntity
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FcmTokenQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findByNotAttendStatus(): List<FcmTokenEntity> {
        val fcm = QFcmTokenEntity.fcmTokenEntity
        val user = QUserEntity.userEntity

        return queryFactory
            .selectFrom(fcm)
            .join(fcm.user, user)
            .where(
                user.currentStatus.eq(AttendanceType.NOT_ATTEND)
            )
            .fetch()
    }
}
