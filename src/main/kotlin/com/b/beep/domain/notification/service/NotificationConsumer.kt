package com.b.beep.domain.notification.service

import com.b.beep.domain.notification.controller.dto.request.SendNotificationRequest
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class NotificationConsumer(
    private val notificationService: NotificationService,
) {
    @RabbitListener(queues = ["\${rabbitmq.notification.queue}"])
    fun listen(request: SendNotificationRequest, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) routingKey: String) {
        val isAll = when (routingKey) {
            "notification.all" -> true
            "notification.not_attended" -> false
            else -> false
        }
        notificationService.sendPushMessage(request, isAll)
    }
}
