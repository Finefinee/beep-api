package com.b.beep.global.common.dto.response

import org.springframework.http.ResponseEntity

data class BaseResponse<T>(
    val data: T,
    val status: Int,
) {
    companion object {
        fun <T> of(data: T, status: Int = 200) =
            ResponseEntity.status(status)
                .body(BaseResponse(data = data, status = status))
    }
}