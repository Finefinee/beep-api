package com.b.beep.domain.student.repository

import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import com.b.beep.domain.attendance.entity.AttendanceEntity
import com.b.beep.domain.attendance.repository.AttendanceRepository
import com.b.beep.domain.user.domain.UserRole
import com.b.beep.domain.user.entity.FixedRoomEntity
import com.b.beep.domain.user.entity.StudentInfoEntity
import com.b.beep.domain.user.entity.UserEntity
import com.b.beep.domain.user.repository.FixedRoomRepository
import com.b.beep.domain.user.repository.StudentInfoRepository
import com.b.beep.domain.user.repository.UserRepository
import com.b.beep.global.config.QueryDslConfigTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.LocalDate

@DataJpaTest
@Import(QueryDslConfigTest::class, StudentQueryRepository::class)
class StudentQueryRepositoryTest @Autowired constructor(
    val studentQueryRepository: StudentQueryRepository,
    val userRepository: UserRepository,
    val studentInfoRepository: StudentInfoRepository,
    val fixedRoomRepository: FixedRoomRepository,
    val attendanceRepository: AttendanceRepository
) {

    lateinit var student: UserEntity

    @BeforeEach
    fun setUp() {
        // 학생 생성
        student = userRepository.save(
            UserEntity(
                email = "test@beep.com",
                username = "테스트학생",
                role = UserRole.STUDENT,
                currentStatus = AttendanceType.CLASS
            )
        )

        // 학생 정보 저장
        studentInfoRepository.save(
            StudentInfoEntity(
                user = student,
                grade = 2,
                cls = 1,
                num = 10
            )
        )

        // 고정실 저장
        fixedRoomRepository.save(
            FixedRoomEntity(
                user = student,
                room = Room.LAB1,
                type = AttendanceType.AFTER_SCHOOL
            )
        )
    }

    @Test
    @DisplayName("학년, 반으로 학생 정보와 오늘 출석 정보를 DTO로 조회한다")
    fun findAllInfoByStatusAndGradeAndCls() {
        // given
        val today = LocalDate.now()
        attendanceRepository.save(
            AttendanceEntity(
                user = student,
                date = today,
                period = 1,
                type = AttendanceType.OUTGOING
            )
        )

        // when
        val results = studentQueryRepository.findAllInfoByStatusAndGradeAndCls(2, 1, null)

        // then
        assertThat(results).hasSize(1)
        val dto = results[0]

        // User 검증
        assertThat(dto.user.id).isEqualTo(student.id)
        assertThat(dto.user.username).isEqualTo("테스트학생")

        // StudentInfo 검증 (num 필드 확인)
        assertThat(dto.studentInfo.num).isEqualTo(10)

        // FixedRooms 검증 (List 이름 변경: fixedRoom -> fixedRooms)
        assertThat(dto.fixedRooms).hasSize(1)
        assertThat(dto.fixedRooms?.get(0)?.room).isEqualTo(Room.LAB1)

        // Attendances 검증 (List 이름 변경: attendance -> attendances)
        assertThat(dto.attendances).hasSize(1)
        assertThat(dto.attendances?.get(0)?.type).isEqualTo(AttendanceType.OUTGOING)
    }

    @Test
    @DisplayName("상태(Status) 조건이 있을 경우 해당 상태의 학생만 조회된다")
    fun findAllInfoByStatusAndGradeAndCls_WithStatus() {
        // given (currentStatus = CLASS)

        // when & then
        val resultEmpty = studentQueryRepository.findAllInfoByStatusAndGradeAndCls(2, 1, AttendanceType.OUTGOING)
        assertThat(resultEmpty).isEmpty()

        val resultPresent = studentQueryRepository.findAllInfoByStatusAndGradeAndCls(2, 1, AttendanceType.CLASS)
        assertThat(resultPresent).hasSize(1)
    }

    @Test
    @DisplayName("고정실(Room)과 타입으로 학생들을 조회한다")
    fun findAllByStatusAndRoomAndType() {
        // given (Room.LAB1, AFTER_SCHOOL)

        // when
        val result = studentQueryRepository.findAllByStatusAndRoomAndType(
            Room.LAB1, // 변경된 Enum 사용
            AttendanceType.AFTER_SCHOOL,
            null
        )

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].username).isEqualTo("테스트학생")
    }
}