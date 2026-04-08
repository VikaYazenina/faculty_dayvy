package com.example.library.entity

import jakarta.persistence.*

// ============================================================================
// ЗАДАНИЕ ФИНАЛ: Создать сущность Reader
// ============================================================================
// ИНСТРУКЦИЯ:
// 1. Добавь @Entity и @Table
// 2. Добавь поля id, name, email

// TODO: Раскомментировать когда будешь делать финальное задание
@Entity
@Table(name = "readers")
data class ReaderEntity(
     @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
     val id: Long = 0,
     val name: String,
     val email: String,

     @ManyToMany
     @JoinTable(
          name = "reader_book",
          joinColumns = [JoinColumn(name = "reader_id")],
          inverseJoinColumns = [JoinColumn(name = "book_id")]
     )
     val books: List<BookEntity> = emptyList()
)
