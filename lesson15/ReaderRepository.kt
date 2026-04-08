package com.example.library.repository

import com.example.library.entity.Reader
import org.springframework.data.jpa.repository.JpaRepository

interface ReaderRepository : JpaRepository<Reader, Long> 
