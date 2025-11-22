package com.b.beep.global.security.jwt.filter

import com.b.beep.global.security.jwt.JwtExtractor
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtExtractor: JwtExtractor) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtExtractor.extractToken(request)

        if (token != null) {
            SecurityContextHolder.getContext().authentication = jwtExtractor.getAuthentication(token)
        }

        filterChain.doFilter(request, response)
    }
}