package dev.n4bb12.spring.graphql.example.authentication

import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller


enum class Role {
  USER,
  ADMIN,
}

data class Me(
  val username: String,
  val roles: List<Role>,
  val superpowers: String?,
  val adminpowers: String?,
)

@PreAuthorize("hasAuthority('USER')")
@Controller
class MeGraphQlController {

  @QueryMapping
  fun me(auth: Authentication): Me {
    return Me(
      auth.name,
      auth.authorities.map { Role.valueOf(it.authority) },
      "SUPER~POWERS",
      "ADMIN~POWERS"
    )
  }

  @SchemaMapping
  fun superpowers(me: Me, auth: Authentication): String? {
    if (auth.authorities.any { it.authority == "ADMIN" }) {
      return me.superpowers
    }
    return null
  }

}
