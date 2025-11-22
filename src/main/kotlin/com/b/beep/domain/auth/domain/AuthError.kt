package com.b.beep.domain.auth.domain

import com.b.beep.global.exception.CustomError
import org.springframework.http.HttpStatus

enum class AuthError(override val status: HttpStatus, override val message: String) : CustomError {
    TOKEN_FETCH_FAILED(HttpStatus.CONFLICT, "dauth 토큰을 불러오는 데 실패했습니다."),
}
