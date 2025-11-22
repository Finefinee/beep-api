package com.b.beep.domain.notification.controller.dto.request

data class SaveTokenRequest(
    val token: String,
    val device: String
)
