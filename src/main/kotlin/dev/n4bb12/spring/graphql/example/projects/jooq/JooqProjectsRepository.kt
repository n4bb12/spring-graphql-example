package dev.n4bb12.spring.graphql.example.projects.jooq

import dev.n4bb12.spring.graphql.example.projects.jpa.Project
import graphql.schema.DataFetchingEnvironment
import org.jooq.DSLContext
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class JooqProjectsRepository(private val create: DSLContext) {

  // TODO jOOQ
  // - Generate types from JPA-annotated classes using the Gradle plugin
  // - Fix "An error ocurred when mapping record to class dev.n4bb12.spring.graphql.example.projects.jpa.Project"
  // - Correctly handle relational fields
  fun findAll(fetchingEnv: DataFetchingEnvironment) = create
    .select(fetchingEnv.selectionSet.fields.map { field(it.name.uppercase()) })
    .from(table("PROJECT"))
    .fetch()
    .also { println(it) }
    .into(Project::class.java)

}

@Configuration
class JooqConfiguration(private val dataSource: DataSource) {

  @Bean
  fun connectionProvider() = DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))

  @Bean
  fun configuration(): DefaultConfiguration {
    val configuration = DefaultConfiguration()
    configuration.set(connectionProvider())
    return configuration
  }

  @Bean
  fun dslContext() = DefaultDSLContext(configuration())

}
