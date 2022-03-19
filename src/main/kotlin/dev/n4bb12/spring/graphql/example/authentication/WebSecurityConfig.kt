package dev.n4bb12.spring.graphql.example.authentication

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
class WebSecurityConfig : WebMvcConfigurer {

  @Bean
  fun securityFilterChain(http: HttpSecurity) = http
    .cors().and()
    .csrf().disable()
    .headers().frameOptions().sameOrigin().and()
    .authorizeRequests { it.anyRequest().permitAll() }
    .httpBasic(withDefaults())
    .build()

  override fun addCorsMappings(registry: CorsRegistry) {
    registry.addMapping("/**")
  }

  companion object {
    @Bean
    fun users(): InMemoryUserDetailsManager {
      val builder = User.withDefaultPasswordEncoder()
      val user = builder.username("user").password("user").authorities("USER").build()
      val admin = builder.username("admin").password("admin").authorities("USER", "ADMIN").build()
      return InMemoryUserDetailsManager(user, admin)
    }
  }
}
