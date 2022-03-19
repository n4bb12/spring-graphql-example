package dev.n4bb12.spring.graphql.example.books

import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller

@Controller
class BooksGraphQlController(private val booksService: BooksService) {

  @QueryMapping
  fun books() = booksService.getAllBooks()

  @MutationMapping
  fun addBook(@Argument input: BookInput) = booksService.addBook(input)

}

@Component
class BooksErrorHandler : DataFetcherExceptionResolverAdapter() {

  override fun resolveToSingleError(ex: Throwable, env: DataFetchingEnvironment): GraphQLError? {
    if (ex is DuplicateBookException) {
      return GraphqlErrorBuilder
        .newError()
        .message(ex.message)
        .errorType(ErrorType.ValidationError)
        .build()
    }
    return null
  }
}
