package com.b.beep.domain.auth.infrastructure

import com.fasterxml.jackson.annotation.JsonProperty

data class GetDAuthTokenRequest(
    @JsonProperty("code") val code: String,
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("client_secret") val clientSecret: String
)
