package com.b.beep.domain.user.controller.dto.response

import com.b.beep.domain.user.entity.StudentInfoEntity

data class StudentInfoResponse(
    val id: Long? = null,
    val grade: Int,
    val cls: Int,
    val num: Int,
    val cardId: String? = null,
) {
    companion object {
        fun of(studentInfo: StudentInfoEntity): StudentInfoResponse {
            return StudentInfoResponse(
                id = studentInfo.id,
                grade = studentInfo.grade,
                cls = studentInfo.cls,
                num = studentInfo.num,
                cardId = studentInfo.cardId,
            )
        }
    }
}
