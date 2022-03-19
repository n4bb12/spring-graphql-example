package dev.n4bb12.spring.graphql.example.authentication

import graphql.language.ArrayValue
import graphql.language.EnumValue
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import org.springframework.security.core.context.SecurityContextHolder

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

@Configuration
class AuthDirectiveConfiguration {
  @Bean
  fun authDirective(): RuntimeWiringConfigurer {
    return RuntimeWiringConfigurer { it.directive("auth", AuthDirectiveWiring()) }
  }
}
