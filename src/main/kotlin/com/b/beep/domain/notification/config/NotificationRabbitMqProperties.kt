package com.b.beep.domain.notification.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "rabbitmq.notification")
data class NotificationRabbitMqProperties(
    val queue: String,
    val exchange: String,
)
