//package com.b.beep.domain.excel.application
//
//import com.b.beep.domain.approve.model.repo.ApprovalJpaRepo
//import com.b.beep.domain.room.domain.enums.RoomName
//import com.b.beep.domain.room.domain.repo.RoomJpaRepo
//import com.b.beep.domain.room.error.RoomError
//import com.b.beep.domain.user.domain.repo.UserAttendJpaRepo
//import com.b.beep.domain.user.domain.repo.UserJpaRepo
//import com.b.beep.global.exception.CustomException
//import org.apache.poi.ss.usermodel.HorizontalAlignment
//import org.apache.poi.ss.usermodel.VerticalAlignment
//import org.apache.poi.ss.util.CellRangeAddress
//import org.apache.poi.xssf.usermodel.XSSFWorkbook
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.cache.annotation.CacheEvict
//import org.springframework.cache.annotation.Cacheable
//import org.springframework.stereotype.Service
//import software.amazon.awssdk.core.sync.RequestBody
//import software.amazon.awssdk.services.s3.S3Client
//import software.amazon.awssdk.services.s3.model.GetObjectRequest
//import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
//import software.amazon.awssdk.services.s3.model.PutObjectRequest
//import software.amazon.awssdk.services.s3.presigner.S3Presigner
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
//import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest
//import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
//import java.io.ByteArrayOutputStream
//import java.time.Duration
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
//
//@Service
//class ExcelUseCase(
//    private val userJpaRepo: UserJpaRepo,
//    private val userAttendJpaRepo: UserAttendJpaRepo,
//    private val s3Presigner: S3Presigner,
//    private val s3Client: S3Client,
//    @Value("\${spring.upload.dir}") private val uploadDir: String,
//    @Value("\${cloud.aws.s3.bucket}") private val bucketName: String,
//    private val approvalJpaRepo: ApprovalJpaRepo,
//    private val roomJpaRepo: RoomJpaRepo
//) {
//
//    /**
//     * 내부 데이터 클래스
//     *
//     * - classification: 반별 분류 (grade-cls)
//     * - studentNumber: 학번 (User의 num 값, 없으면 username)
//     * - studentName: 성명 (User의 name 필드; 없으면 username)
//     * - period8, period10: 각각 8교시, 10교시 출석 상태
//     * - finalStatus: 두 교시 모두 "출석"이면 "출석", 아니면 "미출석"
//     * - room: 실별 출석 시 사용할 실 정보 (User.fixedRoom?.name)
//     */
//    private data class AttendanceRecord(
//        val classification: String,
//        val studentNumber: String,
//        val studentName: String,
//        val period8: String,
//        val period10: String,
//        val finalStatus: String,
//        val room: String = RoomName.OTHER.name
//    )
//
//    /**
//     * 출석 데이터를 바탕으로 엑셀 파일을 생성하여 ByteArray를 반환
//     *
//     * 생성되는 엑셀 파일은 아래 4개 시트를 포함합니다.
//     *
//     * 1. [반별 출석]
//     *    - 헤더: 반별분류, 연번, 학번, 성명, 8교시 출석, 10교시 출석, 최종 출석
//     *    - 그룹: User의 grade와 cls를 조합 ("grade-cls")하고, 그룹별 정렬
//     *
//     * 2. [실별 출석]
//     *    - 헤더: 실, 연번, 학번, 성명, 8교시 출석, 10교시 출석, 최종 출석
//     *    - 그룹: User.fixedRoom (없으면 "미배정") 기준으로 그룹핑
//     *      같은 그룹은 첫 행에 실 값을 표시하고 나머지는 공백으로 처리하며, 그룹 사이에 빈 행 삽입
//     *
//     * 3. [방과후 명단]
//     *    - 헤더: 연번, 학번, 성명, 8교시 출석, 10교시 출석
//     *    - 필터: 8교시 혹은 10교시 상태 중 "방과후"인 경우, 반별(grade-cls) 정렬
//     *
//     * 4. [나르샤 명단]
//     *    - 헤더: 연번, 학번, 성명, 8교시 출석, 10교시 출석
//     *    - 필터: 8교시 혹은 10교시 상태 중 "나르샤"인 경우, 반별(grade-cls) 정렬
//     */
//    fun generateAttendanceExcel(teacherName: String, date: LocalDate): ByteArray {
//        val today = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
//
//
//        // 전체 학생 조회 (User 엔티티 기준)
//        val allUsers = userJpaRepo.findAll()
//        val students = allUsers.filter { it.role.name == "STUDENT" }
//
//        // [반별 출석]용 데이터: classification은 grade와 cls를 이용 ("grade-cls")
//        val banRecords = students.map { student ->
//            val period8Record = userAttendJpaRepo.findByUserAndPeriod(student, 8)
//            val period10Record = userAttendJpaRepo.findByUserAndPeriod(student, 10)
//            val status8 = translateStatus(period8Record?.status?.name)
//            val status10 = translateStatus(period10Record?.status?.name)
//            val finalStatus = if (status8 == "출석" && status10 == "출석") "출석" else "미출석"
//            AttendanceRecord(
//                classification = "${student.grade}-${student.cls}",
//                studentNumber = student.num?.toString() ?: student.username,
//                studentName = try {
//                    student.javaClass.getDeclaredField("name").apply { isAccessible = true }
//                        .get(student)?.toString() ?: student.username
//                } catch (e: Exception) {
//                    student.username
//                },
//                period8 = status8,
//                period10 = status10,
//                finalStatus = finalStatus
//            )
//        }.sortedWith(compareBy({ it.classification }, { it.studentNumber }))
//
//        // [실별 출석]용 데이터: room은 User.fixedRoom?.name (없으면 "미배정")
//        val silRecords = students.map { student ->
//            val period8Record = userAttendJpaRepo.findByUserAndPeriod(student, 8)
//            val period10Record = userAttendJpaRepo.findByUserAndPeriod(student, 10)
//            val status8 = translateStatus(period8Record?.status?.name)
//            val status10 = translateStatus(period10Record?.status?.name)
//            val finalStatus = if (status8 == "출석" && status10 == "출석") "출석" else "미출석"
//            AttendanceRecord(
//                classification = "", // sheet2에서는 사용하지 않음
//                studentNumber = student.num?.toString() ?: student.username,
//                studentName = try {
//                    student.javaClass.getDeclaredField("name").apply { isAccessible = true }
//                        .get(student)?.toString() ?: student.username
//                } catch (e: Exception) {
//                    student.username
//                },
//                period8 = status8,
//                period10 = status10,
//                finalStatus = finalStatus,
//                room = student.fixedRoom?.name?.name ?: RoomName.OTHER.name
//            )
//        }.sortedWith(compareBy({ it.room }, { it.studentNumber }))
//
//        // [방과후 명단] 및 [나르샤 명단]은 banRecords에서 필터 후 반별 정렬
//        val afterSchoolRecords = banRecords.filter { it.period8 == "방과후" || it.period10 == "방과후" }
//            .sortedBy { it.classification }
//        val narshaRecords = banRecords.filter { it.period8 == "나르샤" || it.period10 == "나르샤" }
//            .sortedBy { it.classification }
//
//        val workbook = XSSFWorkbook()
//
//        // 공통 스타일 설정
//        val titleStyle = workbook.createCellStyle().apply {
//            alignment = HorizontalAlignment.CENTER
//            verticalAlignment = VerticalAlignment.CENTER
//            val font = workbook.createFont().apply {
//                bold = true
//                fontHeightInPoints = 16
//            }
//            setFont(font)
//        }
//        val headerStyle = workbook.createCellStyle().apply {
//            alignment = HorizontalAlignment.CENTER
//            verticalAlignment = VerticalAlignment.CENTER
//            val font = workbook.createFont().apply {
//                bold = true
//                fontHeightInPoints = 12
//            }
//            setFont(font)
//        }
//        val dataStyle = workbook.createCellStyle().apply {
//            alignment = HorizontalAlignment.CENTER
//            verticalAlignment = VerticalAlignment.CENTER
//            val font = workbook.createFont().apply {
//                fontHeightInPoints = 11
//            }
//            setFont(font)
//        }
//
//        // ----------------- 시트1: [반별 출석] -----------------
//        val sheetBan = workbook.createSheet("반별 출석")
//        // 컬럼 너비 (총 7개 컬럼)
//        sheetBan.setColumnWidth(0, 256 * 15) // 반별분류
//        sheetBan.setColumnWidth(1, 256 * 10) // 연번 (자동 번호)
//        sheetBan.setColumnWidth(2, 256 * 15) // 학번
//        sheetBan.setColumnWidth(3, 256 * 20) // 성명
//        sheetBan.setColumnWidth(4, 256 * 12) // 8교시 출석
//        sheetBan.setColumnWidth(5, 256 * 12) // 10교시 출석
//        sheetBan.setColumnWidth(6, 256 * 12) // 최종 출석
//
//        // 타이틀 행 (행 0): 전체 컬럼 병합
//        val titleRowBan = sheetBan.createRow(0)
//        sheetBan.addMergedRegion(CellRangeAddress(0, 0, 0, 6))
//        val titleCellBan = titleRowBan.createCell(0)
//        titleCellBan.setCellValue("출석 기록 - 선생님: $teacherName, 날짜: $today - 반별 출석")
//        titleCellBan.cellStyle = titleStyle
//
//        // 헤더 행 (행 2)
//        val headerRowBan = sheetBan.createRow(2)
//        val banHeaders = listOf("반별분류", "연번", "학번", "성명", "8교시 출석", "10교시 출석", "최종 출석")
//        banHeaders.forEachIndexed { index, header ->
//            val cell = headerRowBan.createCell(index)
//            cell.setCellValue(header)
//            cell.cellStyle = headerStyle
//        }
//
//        // 데이터 행 (행 3부터): 그룹별로 처리
//        var currentRowBan = 3
//        // 반별로 그룹핑
//        val banGrouped = banRecords.groupBy { it.classification }.toSortedMap()
//
//        banGrouped.forEach { (classification, records) ->
//            // 먼저 승인 정보 행을 추가
//            val approvalRow = sheetBan.createRow(currentRowBan++)
//
//            // 반별분류 표시
//            approvalRow.createCell(0).apply {
//                setCellValue(classification)
//                cellStyle = dataStyle
//            }
//
//            // 연번, 학번, 성명은 빈 값
//            approvalRow.createCell(1).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//            approvalRow.createCell(2).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//            approvalRow.createCell(3).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//
//            // 8교시 승인 정보 (반별로 승인 정보를 가져오는 로직 필요)
//            approvalRow.createCell(4).apply {
//                val room = roomJpaRepo.findByName(RoomName.valueOf("C${classification.replace('-', '_')}"))!!
//                val approve = approvalJpaRepo.findByPeriodAndRoom(8, room)
//                setCellValue(
//                    "${approve?.approveTeacher?.username}\n${
//                        approve?.createdAt?.format(
//                            DateTimeFormatter.ofPattern(
//                                "HH:mm"
//                            )
//                        )
//                    }"
//                )
//                cellStyle = dataStyle
//            }
//
//            // 10교시 승인 정보
//            approvalRow.createCell(5).apply {
//                val room = roomJpaRepo.findByName(RoomName.valueOf("C${classification.replace('-', '_')}"))!!
//                val approve = approvalJpaRepo.findByPeriodAndRoom(10, room)
//                setCellValue(
//                    "${approve?.approveTeacher?.username}\n${
//                        approve?.createdAt?.format(
//                            DateTimeFormatter.ofPattern(
//                                "HH:mm"
//                            )
//                        )
//                    }"
//                )
//                cellStyle = dataStyle
//            }
//
//            // 최종 출석 승인 정보
//            approvalRow.createCell(6).apply {
//                val room = roomJpaRepo.findByName(RoomName.valueOf("C${classification.replace('-', '_')}"))!!
//                val approve = approvalJpaRepo.findByPeriodAndRoom(11, room)
//                setCellValue(
//                    "${approve?.approveTeacher?.username}\n${
//                        approve?.createdAt?.format(
//                            DateTimeFormatter.ofPattern(
//                                "HH:mm"
//                            )
//                        )
//                    }"
//                )
//                cellStyle = dataStyle
//            }
//
//            // 이제 학생 데이터들을 추가
//            var groupSeq = 1
//            records.forEach { record ->
//                val row = sheetBan.createRow(currentRowBan++)
//
//                // 반별분류는 빈 값 (이미 위에서 표시했으므로)
//                row.createCell(0).apply {
//                    setCellValue("")
//                    cellStyle = dataStyle
//                }
//
//                // 연번: 동일 그룹 내에서 1부터 증가
//                row.createCell(1).apply {
//                    setCellValue(groupSeq.toString())
//                    cellStyle = dataStyle
//                }
//                groupSeq++
//
//                // 학번, 성명, 교시 출석, 최종 출석
//                row.createCell(2).apply {
//                    setCellValue(record.studentNumber)
//                    cellStyle = dataStyle
//                }
//                row.createCell(3).apply {
//                    setCellValue(record.studentName)
//                    cellStyle = dataStyle
//                }
//                row.createCell(4).apply {
//                    setCellValue(record.period8)
//                    cellStyle = dataStyle
//                }
//                row.createCell(5).apply {
//                    setCellValue(record.period10)
//                    cellStyle = dataStyle
//                }
//                row.createCell(6).apply {
//                    setCellValue(record.finalStatus)
//                    cellStyle = dataStyle
//                }
//            }
//
//            // 그룹 사이에 빈 행 삽입 (마지막 그룹 제외)
//            currentRowBan++
//        }
//
//        // ----------------- 시트2: [실별 출석] -----------------
//        // 헤더: "실", "연번", "학번", "성명", "8교시 출석", "10교시 출석", "최종 출석"
//        val sheetSil = workbook.createSheet("실별 출석")
//        sheetSil.setColumnWidth(0, 256 * 15) // 실
//        sheetSil.setColumnWidth(1, 256 * 10) // 연번
//        sheetSil.setColumnWidth(2, 256 * 15) // 학번
//        sheetSil.setColumnWidth(3, 256 * 20) // 성명
//        sheetSil.setColumnWidth(4, 256 * 12) // 8교시 출석
//        sheetSil.setColumnWidth(5, 256 * 12) // 10교시 출석
//        sheetSil.setColumnWidth(6, 256 * 12) // 최종 출석
//
//        // 타이틀 행 (행 0)
//        val titleRowSil = sheetSil.createRow(0)
//        sheetSil.addMergedRegion(CellRangeAddress(0, 0, 0, 6))
//        val titleCellSil = titleRowSil.createCell(0)
//        titleCellSil.setCellValue("출석 기록 - 선생님: $teacherName, 날짜: $today - 실별 출석")
//        titleCellSil.cellStyle = titleStyle
//
//        // 헤더 행 (행 2)
//        val headerRowSil = sheetSil.createRow(2)
//        val silHeaders = listOf(
//            "실", "연번", "학번", "성명",
//            "8교시 출석", "10교시 출석", "최종 출석",
//        )
//        silHeaders.forEachIndexed { index, header ->
//            val cell = headerRowSil.createCell(index)
//            cell.setCellValue(header)
//            cell.cellStyle = headerStyle
//        }
//
//        // 데이터 행: 그룹핑하여, 같은 실은 첫 행에만 실 값을 기입하고 그룹 사이에 빈 행 삽입
//        var currentRowSil = 3
//        // 그룹별로 정렬 (silRecords는 room 기준으로 이미 정렬됨)
//        val silGrouped = silRecords.groupBy { it.room }.toSortedMap()
//        silGrouped.forEach { (roomName, records) ->
//            // 먼저 승인 정보 행을 추가
//            val approvalRow = sheetSil.createRow(currentRowSil++)
//            val room = roomJpaRepo.findByName(RoomName.valueOf(roomName))
//                ?: throw CustomException(RoomError.ROOM_NOT_FOUND)
//
//            // 실 이름 표시
//            approvalRow.createCell(0).apply {
//                setCellValue(roomName)
//                cellStyle = dataStyle
//            }
//
//            approvalRow.createCell(1).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//            approvalRow.createCell(2).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//            approvalRow.createCell(3).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//
//            // 연번, 학번, 성명은 빈 값
//            approvalRow.createCell(4).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//            approvalRow.createCell(5).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//            approvalRow.createCell(6).apply {
//                setCellValue("")
//                cellStyle = dataStyle
//            }
//
//            // 8교시 승인 정보
//            approvalRow.createCell(4).apply {
//                val approve = approvalJpaRepo.findByPeriodAndRoom(8, room)
//                setCellValue(
//                    "${approve?.approveTeacher?.username ?: "없음"}\n${
//                        approve?.updatedAt?.format(
//                            DateTimeFormatter.ofPattern("HH:mm")
//                        )
//                    }"
//                )
//                cellStyle = dataStyle
//            }
//
//            // 10교시 승인 정보
//            approvalRow.createCell(5).apply {
//                val approve = approvalJpaRepo.findByPeriodAndRoom(10, room)
//                setCellValue(
//                    "${approve?.approveTeacher?.username ?: "없음"}\n${
//                        approve?.updatedAt?.format(
//                            DateTimeFormatter.ofPattern("HH:mm")
//                        )
//                    }"
//                )
//                cellStyle = dataStyle
//            }
//
//            // 최종 출석 승인 정보
//            approvalRow.createCell(6).apply {
//                val approve = approvalJpaRepo.findByPeriodAndRoom(11, room)
//                setCellValue(
//                    "${approve?.approveTeacher?.username ?: "없음"}\n${
//                        approve?.updatedAt?.format(
//                            DateTimeFormatter.ofPattern("HH:mm")
//                        )
//                    }"
//                )
//                cellStyle = dataStyle
//            }
//
//            // 이제 학생 데이터들을 추가
//            var groupSeqSil = 1
//            records.forEach { record ->
//                val row = sheetSil.createRow(currentRowSil++)
//
//                // 실 이름은 빈 값 (이미 위에서 표시했으므로)
//                row.createCell(0).apply {
//                    setCellValue("")
//                    cellStyle = dataStyle
//                }
//
//                // 연번
//                row.createCell(1).apply {
//                    setCellValue(groupSeqSil.toString())
//                    cellStyle = dataStyle
//                }
//                groupSeqSil++
//
//                // 학번
//                row.createCell(2).apply {
//                    setCellValue(record.studentNumber)
//                    cellStyle = dataStyle
//                }
//
//                // 성명
//                row.createCell(3).apply {
//                    setCellValue(record.studentName)
//                    cellStyle = dataStyle
//                }
//
//                // 8교시 출석
//                row.createCell(4).apply {
//                    setCellValue(record.period8)
//                    cellStyle = dataStyle
//                }
//
//                // 10교시 출석
//                row.createCell(5).apply {
//                    setCellValue(record.period10)
//                    cellStyle = dataStyle
//                }
//
//                // 최종 출석
//                row.createCell(6).apply {
//                    setCellValue(record.finalStatus)
//                    cellStyle = dataStyle
//                }
//            }
//
//            // 그룹 사이에 빈 행 삽입 (마지막 그룹 제외)
//            currentRowSil++
//        }
//
//        // ----------------- 시트3: [방과후 명단] -----------------
//        val sheetAfter = workbook.createSheet("방과후 명단")
//        sheetAfter.setColumnWidth(0, 256 * 10) // 연번
//        sheetAfter.setColumnWidth(1, 256 * 15) // 학번
//        sheetAfter.setColumnWidth(2, 256 * 20) // 성명
//        sheetAfter.setColumnWidth(3, 256 * 12) // 8교시 출석
//        sheetAfter.setColumnWidth(4, 256 * 12) // 10교시 출석
//
//        // 타이틀 행 (행 0)
//        val titleRowAfter = sheetAfter.createRow(0)
//        sheetAfter.addMergedRegion(CellRangeAddress(0, 0, 0, 4))
//        val titleCellAfter = titleRowAfter.createCell(0)
//        titleCellAfter.setCellValue("출석 기록 - 선생님: $teacherName, 날짜: $today - 방과후 명단")
//        titleCellAfter.cellStyle = titleStyle
//
//        // 헤더 행 (행 2)
//        val headerRowAfter = sheetAfter.createRow(2)
//        val afterHeaders = listOf("연번", "학번", "성명", "8교시 출석", "10교시 출석")
//        afterHeaders.forEachIndexed { index, header ->
//            val cell = headerRowAfter.createCell(index)
//            cell.setCellValue(header)
//            cell.cellStyle = headerStyle
//        }
//        // 데이터 행: 방과후 상태인 기록은 반별(grade-cls) 정렬하여 출력
//        var currentRowAfter = 3
//        var lastClassAfter: String? = null
//        var groupSeqAfter = 1
//        afterSchoolRecords.sortedWith(compareBy({ it.classification }, { it.studentNumber })).forEach { record ->
//            val row = sheetAfter.createRow(currentRowAfter++)
//            if (record.classification != lastClassAfter) {
//                groupSeqAfter = 1
//            }
//            row.createCell(0).apply {
//                setCellValue(groupSeqAfter.toString())
//                cellStyle = dataStyle
//            }
//            groupSeqAfter++
//            lastClassAfter = record.classification
//            row.createCell(1).apply {
//                setCellValue(record.studentNumber)
//                cellStyle = dataStyle
//            }
//            row.createCell(2).apply {
//                setCellValue(record.studentName)
//                cellStyle = dataStyle
//            }
//            row.createCell(3).apply {
//                setCellValue(record.period8)
//                cellStyle = dataStyle
//            }
//            row.createCell(4).apply {
//                setCellValue(record.period10)
//                cellStyle = dataStyle
//            }
//        }
//
//        // ----------------- 시트4: [나르샤 명단] -----------------
//        val sheetNarsha = workbook.createSheet("나르샤 명단")
//        sheetNarsha.setColumnWidth(0, 256 * 10) // 연번
//        sheetNarsha.setColumnWidth(1, 256 * 15) // 학번
//        sheetNarsha.setColumnWidth(2, 256 * 20) // 성명
//        sheetNarsha.setColumnWidth(3, 256 * 12) // 8교시 출석
//        sheetNarsha.setColumnWidth(4, 256 * 12) // 10교시 출석
//
//        // 타이틀 행 (행 0)
//        val titleRowNarsha = sheetNarsha.createRow(0)
//        sheetNarsha.addMergedRegion(CellRangeAddress(0, 0, 0, 4))
//        val titleCellNarsha = titleRowNarsha.createCell(0)
//        titleCellNarsha.setCellValue("출석 기록 - 선생님: $teacherName, 날짜: $today - 나르샤 명단")
//        titleCellNarsha.cellStyle = titleStyle
//
//        // 헤더 행 (행 2)
//        val headerRowNarsha = sheetNarsha.createRow(2)
//        val narshaHeaders = listOf("연번", "학번", "성명", "8교시 출석", "10교시 출석")
//        narshaHeaders.forEachIndexed { index, header ->
//            val cell = headerRowNarsha.createCell(index)
//            cell.setCellValue(header)
//            cell.cellStyle = headerStyle
//        }
//        // 데이터 행: 나르샤 상태인 기록도 반별(grade-cls) 정렬
//        var currentRowNarsha = 3
//        var lastClassNarsha: String? = null
//        var groupSeqNarsha = 1
//        narshaRecords.sortedWith(compareBy({ it.classification }, { it.studentNumber })).forEach { record ->
//            val row = sheetNarsha.createRow(currentRowNarsha++)
//            if (record.classification != lastClassNarsha) {
//                groupSeqNarsha = 1
//            }
//            row.createCell(0).apply {
//                setCellValue(groupSeqNarsha.toString())
//                cellStyle = dataStyle
//            }
//            groupSeqNarsha++
//            lastClassNarsha = record.classification
//            row.createCell(1).apply {
//                setCellValue(record.studentNumber)
//                cellStyle = dataStyle
//            }
//            row.createCell(2).apply {
//                setCellValue(record.studentName)
//                cellStyle = dataStyle
//            }
//            row.createCell(3).apply {
//                setCellValue(record.period8)
//                cellStyle = dataStyle
//            }
//            row.createCell(4).apply {
//                setCellValue(record.period10)
//                cellStyle = dataStyle
//            }
//        }
//
//        val bos = ByteArrayOutputStream()
//        workbook.write(bos)
//        workbook.close()
//        return bos.toByteArray()
//    }
//
//    /**
//     * 생성된 엑셀 파일을 S3에 업로드
//     * 파일명 패턴: "attendance_yyyy-MM-dd.xlsx" (uploadDir 하위에 저장)
//     */
//    @CacheEvict(value = ["excel"], allEntries = true)
//    fun saveAttendanceExcel(teacherName: String, date: LocalDate) {
//        val fileName = "attendance_${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}.xlsx"
//        val fileBytes = generateAttendanceExcel(teacherName, date)
//
//        val putRequest = PutObjectRequest.builder()
//            .bucket(bucketName)
//            .key("$uploadDir/$fileName")
//            .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//            .build()
//
//        s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes))
//    }
//
//    /**
//     * 다운로드 Presigned URL 반환 (유효기간 5분)
//     * date는 "yyyy-MM-dd" 형식의 문자열이어야 합니다.
//     */
//    fun downloadPresignedUrl(date: String): String {
//        val fileName = "attendance_${date}.xlsx"
//        val objectKey = "$uploadDir/$fileName"
//
//        val getObjectRequest = GetObjectRequest.builder()
//            .bucket(bucketName)
//            .key(objectKey)
//            .build()
//
//        val presignRequest = GetObjectPresignRequest.builder()
//            .signatureDuration(Duration.ofMinutes(5))
//            .getObjectRequest(getObjectRequest)
//            .build()
//
//        val presignedRequest: PresignedGetObjectRequest = s3Presigner.presignGetObject(presignRequest)
//        return presignedRequest.url().toString()
//    }
//
//    /**
//     * 업로드 Presigned URL 반환 (테스트용, 유효기간 5분)
//     */
//    fun uploadPresignedUrl(fileName: String): String {
//        val objectKey = "$uploadDir/$fileName"
//        val putObjectRequest = PutObjectRequest.builder()
//            .bucket(bucketName)
//            .key(objectKey)
//            .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//            .build()
//
//        val presignRequest = PutObjectPresignRequest.builder()
//            .signatureDuration(Duration.ofMinutes(5))
//            .putObjectRequest(putObjectRequest)
//            .build()
//
//        val presignedRequest = s3Presigner.presignPutObject(presignRequest)
//        return presignedRequest.url().toString()
//    }
//
//    /**
//     * 지정된 연도, 월에 해당하는 파일 목록을 S3에서 반환
//     * 파일명 패턴: "attendance_yyyy-MM-dd.xlsx"
//     */
//    @Cacheable("excel")
//    fun listAttendanceFiles(year: Int, month: Int): List<String> {
//        val prefix = "$uploadDir/attendance_${year}-${String.format("%02d", month)}"
//
//        val listRequest = ListObjectsV2Request.builder()
//            .bucket(bucketName)
//            .prefix(prefix)
//            .build()
//
//        val listResponse = s3Client.listObjectsV2(listRequest)
//        return listResponse.contents()
//            .filter {
//                it.key().matches(Regex("${uploadDir}/attendance_${year}-${String.format("%02d", month)}-\\d{2}\\.xlsx"))
//            }
//            .sortedBy { obj ->
//                val dateString = obj.key().substringAfter("attendance_").substring(0, 10)
//                LocalDate.parse(dateString)
//            }
//            .map { it.key().replace("$uploadDir/", "") }
//    }
//
//    private fun translateStatus(attendStatus: String?): String {
//        return when (attendStatus) {
//            "ATTEND" -> "출석"
//            "NOT_ATTEND" -> "미출석"
//            "SLEEPOVER" -> "외박"
//            "OUTGOING" -> "외출"
//            "AFTER_SCHOOL" -> "방과후"
//            "NARSHA" -> "나르샤"
//            "FIELD_PRACTICE" -> "현장실습"
//            "SHIFT_ATTEND" -> "실 이동 출석"
//            "SHIFT_NOT_ATTEND" -> "실 이동 미출석"
//            null -> "미기록"
//            else -> "기타"
//        }
//    }
//}
