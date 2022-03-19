package dev.n4bb12.spring.graphql.example.vehicle

import graphql.schema.Coercing
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.stereotype.Controller
import java.time.Instant


data class Coordinates(val x: Int, val y: Int)

data class Vehicle(
  val coordinates: Coordinates,
  val lastSeen: Instant
)

@Controller
class VehicleGraphQlController {

  @QueryMapping
  fun vehicle() = Vehicle(Coordinates(12, 34), Instant.now())

}

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

@Configuration
class CoordinatesScalarConfiguration {
  @Bean
  fun coordinatesScalar(): RuntimeWiringConfigurer {
    val scalar = GraphQLScalarType.newScalar().name("Coordinates").coercing(CoordinatesCoercing()).build()
    return RuntimeWiringConfigurer { it.scalar(scalar) }
  }
}
