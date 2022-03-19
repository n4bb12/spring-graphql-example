package dev.n4bb12.spring.graphql.example.complexity

import graphql.analysis.MaxQueryComplexityInstrumentation
import graphql.analysis.MaxQueryDepthInstrumentation
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQlComplexityConfig {

  @Bean
  fun complexity() = MaxQueryComplexityInstrumentation(1000)

  @Bean
  fun depth() = MaxQueryDepthInstrumentation(20)

}
