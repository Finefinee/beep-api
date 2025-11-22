//package com.b.beep.domain.excel.presentation
//
//import com.b.beep.domain.excel.application.ExcelUseCase
//import io.swagger.v3.oas.annotations.Operation
//import io.swagger.v3.oas.annotations.tags.Tag
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
//@Tag(name = "엑셀")
//@RestController
//@RequestMapping("/excel")
//class AttendanceExcelController(
//    private val excelUseCase: ExcelUseCase
//) {
//    @Operation(summary = "파일 조회", description = "yyyy-MM 기준으로 파일을 조회합니다.")
//    @GetMapping("/files")
//    fun listAttendanceFiles(@RequestParam year: Int, @RequestParam month: Int) =
//        excelUseCase.listAttendanceFiles(year, month)
//
//    @Operation(summary = "다운로드 url 요청", description = "yyyy-MM-dd 형식으로 특정 날짜의 파일을 다운로드 할수 있는 주소를 반환")
//    @GetMapping("/presigned-url")
//    fun getPresignedUrl(@RequestParam date: String): ResponseEntity<String> {
//        val url = excelUseCase.downloadPresignedUrl(date)
//        return ResponseEntity.ok(url)
//    }
//
//    @Operation(
//        summary = "업로드 url 요청",
//        description = "attendance_yyyy-MM-dd.xlsx 형식 파일 형식으로 업로드 할수 있는 주소를 반환  \n  반환받은 주소에 put 으롵 파일 업로드"
//    )
//    @PostMapping("/presigned-url")
//    fun generatePresignedUrl(@RequestParam filName: String): ResponseEntity<String> {
//        val url = excelUseCase.uploadPresignedUrl(filName)
//        return ResponseEntity.ok(url)
//    }
//}
