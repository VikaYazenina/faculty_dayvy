package com.example.library.service

import com.example.library.entity.Book
import com.example.library.entity.Reader
import com.example.library.repository.BookRepository
import com.example.library.repository.ReaderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReaderService(
    private val readerRepository: ReaderRepository,
    private val bookRepository: BookRepository
) {

    @Transactional(readOnly = true)
    fun getAllReaders(): List<Reader> {
        return readerRepository.findAll()
    }

    @Transactional
    fun addReader(name: String, email: String, bookIds: List<Long>): Reader {
        val books: MutableSet<Book> = bookRepository.findAllById(bookIds).toMutableSet()
        val reader = Reader(name = name, email = email, books = books)
        return readerRepository.save(reader)
    }
}