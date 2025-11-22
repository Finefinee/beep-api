package com.b.beep.domain.attendance.controller

import com.b.beep.domain.attendance.controller.docs.AttendanceDocs
import com.b.beep.domain.attendance.controller.dto.request.AttendRequest
import com.b.beep.domain.attendance.service.AttendanceService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/attend")
class AttendanceController(
    private val attendanceService: AttendanceService
) : AttendanceDocs {
    @PostMapping
    override fun attend(@RequestBody request: AttendRequest) {
        attendanceService.attend(request)
    }

    @PostMapping("/cancel")
    override fun cancelAttendance() {
        attendanceService.cancelAttendance()
    }
}
