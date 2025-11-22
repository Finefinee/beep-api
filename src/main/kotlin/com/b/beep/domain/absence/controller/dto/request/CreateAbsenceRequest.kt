package com.b.beep.domain.absence.controller.dto.request

import java.time.LocalDate

data class CreateAbsenceRequest(
    val grade: Int,
    val cls: Int,
    val num: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: String
)
