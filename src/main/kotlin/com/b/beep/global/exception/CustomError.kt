package com.b.beep.global.exception

import org.springframework.http.HttpStatus

interface CustomError {
    val status: HttpStatus
    val message: String
}