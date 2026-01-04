package com.b.beep.domain.student.repository

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import com.b.beep.domain.attendance.entity.QAttendanceEntity.attendanceEntity
import com.b.beep.domain.student.controller.dto.QStudentQueryDto
import com.b.beep.domain.student.controller.dto.StudentQueryDto
import com.b.beep.domain.user.domain.UserRole
import com.b.beep.domain.user.entity.QFixedRoomEntity.fixedRoomEntity
import com.b.beep.domain.user.entity.QStudentInfoEntity.studentInfoEntity
import com.b.beep.domain.user.entity.QUserEntity.userEntity
import com.b.beep.domain.user.entity.UserEntity
import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class StudentQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    fun findAllByStatusAndGradeAndCls(
        grade: Int,
        cls: Int,
        status: AttendanceType? = null,
    ): List<UserEntity> {

        return queryFactory
            .selectFrom(userEntity)
            .join(studentInfoEntity)
            .on(studentInfoEntity.user.id.eq(userEntity.id))
            .where(
                studentInfoEntity.grade.eq(grade),
                studentInfoEntity.cls.eq(cls),
                status?.let { userEntity.currentStatus.eq(it) },
                userEntity.role.eq(UserRole.STUDENT)
            )
            .fetch()
    }
    
    fun findAllByStatusAndRoomAndType(
        room: Room,
        type: AttendanceType,
        status: AttendanceType? = null,
    ): List<UserEntity> {

        return queryFactory
            .selectFrom(userEntity)
            .join(fixedRoomEntity)
            .on(fixedRoomEntity.user.id.eq(userEntity.id))
            .where(
                fixedRoomEntity.room.eq(room),
                fixedRoomEntity.type.eq(type),
                status?.let { userEntity.currentStatus.eq(it) },
                userEntity.role.eq(UserRole.STUDENT)
            )
            .fetch()
    }

    fun findAllInfoByStatusAndRoomAndType(
        room: Room,
        type: AttendanceType,
        status: AttendanceType? = null
    ): List<StudentQueryDto> {
        return queryFactory
            .from(userEntity)
            .innerJoin(studentInfoEntity).on(studentInfoEntity.user.eq(userEntity))
            .innerJoin(fixedRoomEntity).on(
                fixedRoomEntity.user.eq(userEntity),
                fixedRoomEntity.room.eq(room),
                fixedRoomEntity.type.eq(type)
            )
            .leftJoin(attendanceEntity).on(
                attendanceEntity.user.eq(userEntity),
                attendanceEntity.date.eq(LocalDate.now())
            )
            .where(
                status?.let { userEntity.currentStatus.eq(it) },
                userEntity.role.eq(UserRole.STUDENT)
            )
            .transform(
                groupBy(userEntity.id).list(
                    QStudentQueryDto(
                        userEntity,
                        studentInfoEntity,
                        list(fixedRoomEntity),
                        list(attendanceEntity)
                    )
                )
            )
    }

    fun findAllInfoByStatusAndGradeAndCls(
        grade: Int,
        cls: Int,
        status: AttendanceType? = null
    ): List<StudentQueryDto> {
        return queryFactory
            .from(userEntity)
            .innerJoin(studentInfoEntity).on(studentInfoEntity.user.eq(userEntity))
            .leftJoin(fixedRoomEntity).on(fixedRoomEntity.user.eq(userEntity))
            .leftJoin(attendanceEntity).on(
                attendanceEntity.user.eq(userEntity),
                attendanceEntity.date.eq(LocalDate.now())
            )
            .where(
                studentInfoEntity.grade.eq(grade),
                studentInfoEntity.cls.eq(cls),
                status?.let { userEntity.currentStatus.eq(it) },
                userEntity.role.eq(UserRole.STUDENT)
            )
            .transform(
                groupBy(userEntity.id).list(
                    QStudentQueryDto(
                        userEntity,
                        studentInfoEntity,
                        list(fixedRoomEntity),
                        list(attendanceEntity)
                    )
                )
            )
    }

    fun findAllInfoByRoomTypeStatusAndRole(
        fixedRoomType: AttendanceType,
        status: AttendanceType,
        role: UserRole
    ): List<StudentQueryDto> {
        return queryFactory
            .from(userEntity)
            .innerJoin(studentInfoEntity).on(studentInfoEntity.user.eq(userEntity))
            .innerJoin(fixedRoomEntity).on(
                fixedRoomEntity.user.eq(userEntity),
                fixedRoomEntity.type.eq(fixedRoomType)
            )
            .leftJoin(attendanceEntity).on(
                attendanceEntity.user.eq(userEntity),
                attendanceEntity.date.eq(LocalDate.now())
            )
            .where(
                userEntity.currentStatus.eq(status),
                userEntity.role.eq(role)
            )
            .transform(
                groupBy(userEntity.id).list(
                    QStudentQueryDto(
                        userEntity,
                        studentInfoEntity,
                        list(fixedRoomEntity),
                        list(attendanceEntity)
                    )
                )
            )
    }

}
