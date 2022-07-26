package dev.n4bb12.spring.graphql.example.dgs

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsDirective
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.DgsScalar
import com.netflix.graphql.dgs.InputArgument
import dev.n4bb12.spring.graphql.example.books.BookInput
import dev.n4bb12.spring.graphql.example.books.BooksService
import dev.n4bb12.spring.graphql.example.dgs.types.Greeting
import dev.n4bb12.spring.graphql.example.dgs.types.Me
import dev.n4bb12.spring.graphql.example.dgs.types.Role
import dev.n4bb12.spring.graphql.example.greeting.Case
import dev.n4bb12.spring.graphql.example.projects.exposed.ExposedProjectsRepository
import dev.n4bb12.spring.graphql.example.vehicle.Coordinates
import dev.n4bb12.spring.graphql.example.vehicle.Vehicle
import graphql.language.ArrayValue
import graphql.language.EnumValue
import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.time.Instant
import java.util.Locale

@DgsComponent
class DgsGraphQlController(
  private val booksService: BooksService,
  private val projectsRepository: ExposedProjectsRepository,
) {

  @DgsQuery
  fun books() = booksService.getAllBooks()

  @DgsMutation
  fun addBook(@InputArgument input: BookInput) = booksService.addBook(input)

  @DgsQuery
  fun vehicle() = Vehicle(Coordinates(12, 34), Instant.now())

  @DgsQuery
  fun hello(@InputArgument subject: String): Greeting {
    val text = "Hello $subject!"
    return Greeting(text, text, text)
  }

  @DgsQuery
  fun projects(fetchingEnv: DataFetchingEnvironment) = projectsRepository.findAll(fetchingEnv)

  @PreAuthorize("hasAuthority('USER')")
  @DgsMutation
  fun me(auth: Authentication): Me {
    return Me(
      auth.name,
      auth.authorities.map { Role.valueOf(it.authority) },
      "SUPER~POWERS",
      "ADMIN~POWERS"
    )
  }

  @PreAuthorize("hasAuthority('USER')")
  @DgsData(parentType = "Me")
  fun superpowers(me: Me, auth: Authentication): String? {
    if (auth.authorities.any { it.authority == "ADMIN" }) {
      return me.superpowers
    }
    return null
  }

}

@DgsScalar(name = "Coordinates")
class CoordinatesCoercing : Coercing<Coordinates, String> {
  override fun serialize(input: Any): String {
    if (input is Coordinates) {
      return "[${input.x}, ${input.y}]"
    }
    throw CoercingSerializeException()
  }

  override fun parseValue(input: Any): Coordinates {
    return Coordinates(0, 0)
  }

  override fun parseLiteral(input: Any): Coordinates {
    return Coordinates(0, 0)
  }
}

@DgsDirective(name = "case")
class CaseDirectiveWiring : SchemaDirectiveWiring {
  override fun onField(wiringEnv: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition?>): GraphQLFieldDefinition? {
    val parentType = wiringEnv.fieldsContainer
    val field = wiringEnv.element
    val originalFetcher = wiringEnv.codeRegistry.getDataFetcher(parentType, field)
    val value = wiringEnv.directive.getArgument("case").argumentValue.value as EnumValue
    val caseName = value.name

    val dataFetcher = DataFetcherFactories.wrapDataFetcher(
      originalFetcher
    ) { fetchingEnv: DataFetchingEnvironment?, value: Any? ->
      if (value !is String) {
        value
      } else if (caseName == Case.UPPER.name) {
        value.toString().uppercase(Locale.getDefault())
      } else if (caseName == Case.LOWER.name) {
        value.toString().lowercase(Locale.getDefault())
      } else {
        throw Error("Unsupported case: $caseName")
      }
    }

    wiringEnv.codeRegistry.dataFetcher(parentType, field, dataFetcher)
    return field
  }
}

@DgsDirective(name = "auth")
class AuthDirectiveWiring : SchemaDirectiveWiring {
  override fun onField(wiringEnv: SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition?>): GraphQLFieldDefinition? {
    val parentType = wiringEnv.fieldsContainer
    val field = wiringEnv.element
    val originalFetcher = wiringEnv.codeRegistry.getDataFetcher(parentType, field)
    val value = wiringEnv.directive.getArgument("requiredRoles").argumentValue.value as ArrayValue
    val roles = value.values.map { (it as EnumValue) }

    val authDataFetcher = DataFetcher { fetchingEnv ->
      val auth = SecurityContextHolder.getContext().authentication

      if (roles.all { role -> auth.authorities.any { it.authority == role.name } }) {
        originalFetcher.get(fetchingEnv)
      } else {
        null
      }
    }

    return wiringEnv.setFieldDataFetcher(authDataFetcher)
  }
}
