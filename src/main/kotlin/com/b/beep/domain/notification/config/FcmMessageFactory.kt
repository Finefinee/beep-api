package com.b.beep.domain.notification.config

import com.b.beep.domain.notification.controller.dto.request.SendNotificationRequest
import com.google.firebase.messaging.*
import org.springframework.stereotype.Component

@Component
class FcmMessageFactory {
    fun createMessage(request: SendNotificationRequest, token: String): Message {
        return Message.builder().apply {
            setToken(token)
            setNotification(createNotificationFrom(request))
            setAndroidConfig(createDefaultAndroidConfig())
            setApnsConfig(createDefaultApnsConfig())
        }.build()
    }

    private fun createNotificationFrom(request: SendNotificationRequest): Notification {
        return Notification.builder().apply {
            setTitle(request.title)
            setBody(request.body)
            setImage(request.imageUrl)
        }.build()
    }

    private fun createDefaultAndroidConfig(): AndroidConfig {
        return AndroidConfig.builder()
            .setNotification(
                AndroidNotification.builder()
                    .setSound("default")
                    .build()
            )
            .build()
    }

    private fun createDefaultApnsConfig(): ApnsConfig {
        return ApnsConfig.builder()
            .setAps(
                Aps.builder()
                    .setSound("default")
                    .build()
            ).build()
    }
}
