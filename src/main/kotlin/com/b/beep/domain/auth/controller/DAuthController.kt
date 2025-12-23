package com.b.beep.domain.auth.controller

import com.b.beep.domain.auth.controller.docs.DAuthDocs
import com.b.beep.domain.auth.controller.dto.request.LoginRequest
import com.b.beep.domain.auth.service.AuthService
import com.b.beep.global.common.dto.response.BaseResponse
import com.b.beep.global.security.jwt.dto.response.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/dauth")
class DAuthController(
    private val authService: AuthService
) : DAuthDocs {
    @PostMapping("/login")
    override fun login(@RequestBody request: LoginRequest): ResponseEntity<BaseResponse<TokenResponse>> {
        return BaseResponse.of(authService.login(request))
    }
}
