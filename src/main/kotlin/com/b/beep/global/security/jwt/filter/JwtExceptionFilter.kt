package com.b.beep.global.security.jwt.filter

import com.b.beep.global.security.jwt.error.JwtError
import com.b.beep.global.exception.CustomException
import com.b.beep.global.exception.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            response.sendError(CustomException(JwtError.EXPIRED_TOKEN))
        } catch (e: MalformedJwtException) {
            e.printStackTrace()
            response.sendError(CustomException(JwtError.MALFORMED_TOKEN))
        } catch (e: CustomException) {
            response.sendError(e)
        }
    }

    private fun HttpServletResponse.sendError(exception: CustomException) {
        val error = exception.error

        status = error.status.value()

        outputStream.use {
            it.write(objectMapper.writeValueAsBytes(ErrorResponse.of(exception).body))
            it.flush()
        }
    }
}