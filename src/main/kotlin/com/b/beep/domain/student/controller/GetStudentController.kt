package com.b.beep.domain.student.controller

import com.b.beep.domain.student.controller.docs.GetStudentDocs
import com.b.beep.domain.student.controller.dto.response.StudentResponse
import com.b.beep.domain.student.service.GetStudentService
import com.b.beep.domain.attendance.domain.enums.AttendanceType
import com.b.beep.domain.attendance.domain.enums.Room
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students")
class GetStudentController(
    private val getStudentService: GetStudentService
) : GetStudentDocs {
    @GetMapping("/room")
    @ResponseStatus(HttpStatus.OK)
    override fun getAllByRoomAndType(
        @RequestParam room: Room,
        @RequestParam type: AttendanceType
    ): List<StudentResponse> {
        return getStudentService.getAllByRoomAndType(room, type)
    }

    @GetMapping("/class")
    @ResponseStatus(HttpStatus.OK)
    override fun getAllByGradeAndCls(
        @RequestParam grade: Int,
        @RequestParam cls: Int,
    ): List<StudentResponse> {
        return getStudentService.getAllByGradeAndCls(grade, cls)
    }
}
