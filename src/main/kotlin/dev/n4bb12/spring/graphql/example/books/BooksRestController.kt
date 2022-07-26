package dev.n4bb12.spring.graphql.example.books

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
