package com.example.demo1

import java.time.LocalDateTime
data class Product (
    val id: Long,
    var name: String,
    var category: String,
    var price: Double,
    val created: LocalDateTime=LocalDateTime.now(),
    val updated: LocalDateTime=LocalDateTime.now()
)
