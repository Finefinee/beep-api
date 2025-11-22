package com.b.beep.domain.notification.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class NotificationRabbitMqConfig(
    private val notificationRabbitMqProperties: NotificationRabbitMqProperties
) {
    @Bean
    fun notificationExchange(): TopicExchange {
        return TopicExchange(notificationRabbitMqProperties.exchange)
    }

    @Bean
    fun notificationQueue(): Queue {
        return Queue(notificationRabbitMqProperties.queue, false)
    }

    @Bean
    fun notificationBinding(notificationQueue: Queue, notificationExchange: TopicExchange): Binding {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with("notification.*")
    }
}
