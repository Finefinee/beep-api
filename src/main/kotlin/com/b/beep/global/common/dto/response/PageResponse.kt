package com.b.beep.global.common.dto.response

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val currentPage: Int,
)