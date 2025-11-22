package com.b.beep.domain.student.controller.docs

import com.b.beep.domain.student.controller.dto.response.StudentResponse
import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag

@Tag(name = "학생 조회")
interface GetStudentDocs {
    @Operation(summary = "실별 학생 조회")
    fun getAllByRoomAndType(room: Room, type: AttendanceType): List<StudentResponse>

    @Operation(summary = "반별 학생 조회")
    fun getAllByGradeAndCls(grade: Int, cls: Int): List<StudentResponse>
}
