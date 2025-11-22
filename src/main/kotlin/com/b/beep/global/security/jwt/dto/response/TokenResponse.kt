package com.b.beep.global.security.jwt.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
)