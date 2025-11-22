package com.b.beep.domain.notification.controller.dto.request

data class SendNotificationRequest(
    val title: String,
    val body: String,
    val imageUrl: String,
)