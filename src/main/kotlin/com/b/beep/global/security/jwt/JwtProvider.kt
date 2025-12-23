package com.b.beep.global.security.jwt

import com.b.beep.domain.auth.repository.RefreshTokenRepository
import com.b.beep.global.security.jwt.config.JwtProperties
import com.b.beep.global.security.jwt.dto.response.TokenResponse
import com.b.beep.global.security.jwt.enums.JwtType
import io.jsonwebtoken.Header
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey


@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(email: String): TokenResponse {
        return TokenResponse(
            accessToken = generateAccessToken(email),
            refreshToken = generateRefreshToken(email)
        )
    }

    fun generateAccessToken(email: String): String {
        val now = Date()

        return Jwts.builder()
            .setHeaderParam(Header.JWT_TYPE, JwtType.ACCESS)
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + jwtProperties.accessExp))
            .signWith(getSigningKey())
            .compact()
    }

    fun generateRefreshToken(email: String): String {
        val now = Date()
        val refreshToken = Jwts.builder()
            .setHeaderParam(Header.JWT_TYPE, JwtType.REFRESH)
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(Date(now.time + jwtProperties.refreshExp))
            .signWith(getSigningKey())
            .compact()
        refreshTokenRepository.save(email,refreshToken)

        return refreshToken
    }
}