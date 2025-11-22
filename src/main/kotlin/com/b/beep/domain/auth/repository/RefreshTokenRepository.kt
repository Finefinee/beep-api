package com.b.beep.domain.auth.repository

interface RefreshTokenRepository {
    fun save(email: String, refreshToken: String)
    fun findByUserId(email: String): String?
    fun delete(email: String)
}
