package com.b.beep.domain.user.controller.docs

import com.b.beep.domain.user.controller.dto.response.UserResponse
import com.b.beep.global.common.dto.response.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "사용자", description = "사용자 관련 API")
interface UserDocs {
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    fun getMe(): ResponseEntity<BaseResponse<UserResponse>>
}
