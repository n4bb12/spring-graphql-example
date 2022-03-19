package dev.n4bb12.spring.graphql.example.books

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
class BooksRestController(private val booksService: BooksService) {

  @GetMapping("/books")
  fun books() = booksService.getAllBooks()

  @PostMapping("/books")
  fun addBook(@RequestBody input: BookInput) = booksService.addBook(input)

  @ResponseStatus(value = HttpStatus.CONFLICT)
  @ExceptionHandler(DuplicateBookException::class)
  fun duplicateBook() {
  }

}
