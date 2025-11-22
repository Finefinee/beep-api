package com.b.beep.domain.student.controller

import com.b.beep.domain.student.controller.dto.request.StudentPreAttendRequest
import com.b.beep.domain.student.controller.dto.request.UpdateStudentStatusRequest
import com.b.beep.domain.student.service.StudentManagementService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/students")
class StudentManagementController(
    private val studentManagementService: StudentManagementService
) {
    @PatchMapping("/pre-attend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun saveStudent(@RequestBody request: StudentPreAttendRequest) {
        studentManagementService.createPresetAttendance(
            grade = request.grade,
            cls = request.cls,
            num = request.num,
            status = request.status,
            period = request.period
        )
    }

    @PatchMapping("/attend-status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun updateStudent(@RequestBody request: UpdateStudentStatusRequest) {
        studentManagementService.updateCurrentStudentStatus(
            grade = request.grade,
            cls = request.cls,
            num = request.num,
            status = request.status
        )
    }
}