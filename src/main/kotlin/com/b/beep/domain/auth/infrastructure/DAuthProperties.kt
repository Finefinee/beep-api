package com.b.beep.domain.auth.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "dauth")
data class DAuthProperties(
    val clientId: String,
    val clientSecret: String
)
