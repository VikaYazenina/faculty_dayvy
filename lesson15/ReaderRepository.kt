package com.example.library.repository

import com.example.library.entity.Reader
import org.springframework.data.jpa.repository.JpaRepository

interface ReaderRepository : JpaRepository<Reader, Long> {
    @Query("SELECT r FROM ReaderEntity r LEFT JOIN FETCH r.books")
    fun findAllWithBooks(): List<ReaderEntity>
}