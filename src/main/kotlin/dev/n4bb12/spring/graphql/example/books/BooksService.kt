package dev.n4bb12.spring.graphql.example.books

import org.springframework.stereotype.Component
import java.util.*

data class Book(val id: UUID, val title: String, val authorName: String)

data class BookInput(val title: String, val authorName: String)

class DuplicateBookException(input: BookInput) : RuntimeException("This book already exists: ${input.title}")

@Component
class BooksService {

  val book1 = Book(UUID.randomUUID(), "Slow Cooking", "Hans Gerlach")
  val book2 = Book(UUID.randomUUID(), "Speed Cooking", "Sandra Schumann")
  val books = mutableListOf(book1, book2)

  fun getAllBooks() = books

  fun addBook(input: BookInput): Book {
    if (books.any { it.title == input.title }) {
      throw DuplicateBookException(input)
    }

    val book = Book(UUID.randomUUID(), input.title, input.authorName)
    books.add(book)
    return book
  }

}
