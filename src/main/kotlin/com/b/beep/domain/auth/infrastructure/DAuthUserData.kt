package com.b.beep.domain.auth.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class DAuthUserData(
    @JsonProperty("uniqueId")
    val uniqueId: String,
    val grade: Int,
    val room: Int,
    val number: Int,
    val name: String,
    @JsonProperty("profileImage")
    val profileImage: String?,
    val role: String,
    val email: String
)
