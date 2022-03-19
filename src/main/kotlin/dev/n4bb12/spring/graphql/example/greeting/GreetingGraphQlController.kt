package dev.n4bb12.spring.graphql.example.greeting

import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

data class Greeting(
  val first: String,
  val second: String,
  val third: String,
)

@Controller
class GreetingGraphQlController {

  @QueryMapping
  fun hello(@Argument subject: String): Greeting {
    val text = "Hello $subject!"
    return Greeting(text, text, text)
  }

}
