package com.b.beep.domain.user.controller

import com.b.beep.domain.user.controller.docs.FixedRoomDocs
import com.b.beep.domain.user.controller.dto.request.AddFixedRoomRequest
import com.b.beep.domain.user.controller.dto.request.UpdateFixedRoomRequest
import com.b.beep.domain.user.controller.dto.response.FixedRoomResponse
import com.b.beep.domain.user.service.FixedRoomService
import com.b.beep.global.common.dto.response.BaseResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/fixed-rooms")
class FixedRoomController(
    private val fixedRoomService: FixedRoomService,
) : FixedRoomDocs {
    @PostMapping
    override fun addFixedRoom(@RequestBody request: AddFixedRoomRequest) {
        fixedRoomService.add(request)
    }

    @GetMapping
    override fun getFixedRooms(): ResponseEntity<BaseResponse<List<FixedRoomResponse>>> {
        return BaseResponse.of(fixedRoomService.getAll().map { FixedRoomResponse.of(it) })
    }

    @PatchMapping("/{fixedRoomId}")
    override fun updateFixedRoom(@PathVariable fixedRoomId: Long, @RequestBody request: UpdateFixedRoomRequest) {
        fixedRoomService.update(fixedRoomId, request)
    }

    @DeleteMapping("/{fixedRoomId}")
    override fun deleteFixedRoom(@PathVariable fixedRoomId: Long) {
        fixedRoomService.delete(fixedRoomId)
    }
}