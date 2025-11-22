package com.b.beep.global.security.jwt.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "application.jwt")
data class JwtProperties(
    val accessExp: Long,
    val refreshExp: Long,
    val header: String,
    val prefix: String,
    val secretKey: String,
)
