package com.b.beep.domain.absence.controller.dto.request

import java.time.LocalDate

data class UpdateAbsenceRequest(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val reason: String
)
