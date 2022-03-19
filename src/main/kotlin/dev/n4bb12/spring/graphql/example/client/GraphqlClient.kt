package dev.n4bb12.spring.graphql.example.client

import com.apollographql.apollo3.ApolloClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GraphqlClient(val apolloClient: ApolloClient) {

  @GetMapping("/greet/{subject}")
  suspend fun greet(@PathVariable subject: String): GreetQuery.Data? {
    val query = GreetQuery(subject)
    return apolloClient.query(query).execute().data
  }

}

@Configuration
class ApolloClientConfiguration {
  @Bean
  fun apolloClient() = ApolloClient.Builder()
    .serverUrl("http://localhost:8080/graphql")
    .build()
}
