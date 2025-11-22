package com.b.beep.domain.auth.infrastructure

data class DAuthUserResponse(
    val status: Int,
    val message: String,
    val data: DAuthUserData
)
