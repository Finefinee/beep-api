package com.b.beep.domain.memo.repository

import com.b.beep.domain.memo.entity.MemoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemoRepository : JpaRepository<MemoEntity, Long>
