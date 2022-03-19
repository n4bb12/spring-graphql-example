package dev.n4bb12.spring.graphql.example.greeting

import graphql.language.EnumValue
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer
import java.util.*

enum class Case {
  UPPER,
  LOWER
}

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

@Configuration
class CaseDirectiveConfiguration {
  @Bean
  fun caseDirective(): RuntimeWiringConfigurer {
    return RuntimeWiringConfigurer { it.directive("case", CaseDirectiveWiring()) }
  }
}
