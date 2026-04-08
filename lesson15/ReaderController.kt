package com.example.library.controller

import com.example.library.entity.Reader
import com.example.library.service.ReaderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/library/readers")
class ReaderController(private val readerService: ReaderService) {

    @GetMapping
    fun getAllReaders(): List<Reader> {
        return readerService.getAllReaders()
    }

    
}

data class AddReaderRequest(
    val name: String,
    val email: String,
    val bookIds: List<Long>
)