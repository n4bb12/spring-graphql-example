package dev.n4bb12.spring.graphql.example.projects.exposed

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class ExposedProjectsInitializer(private val dataSource: DataSource) : CommandLineRunner {

  override fun run(vararg args: String?) {
    Database.connect(dataSource)

    transaction {
      addLogger(StdOutSqlLogger)

      SchemaUtils.create(ExposedDevelopersTable, ExposedProjectDevelopersJoinTable, ExposedProjectsTable)

      val dev1 = DeveloperDao.new { firstName = "Josh"; lastName = "Long" }
      val dev2 = DeveloperDao.new { firstName = "James"; lastName = "Ward" }
      val dev3 = DeveloperDao.new { firstName = "Oliver"; lastName = "Gierke" }

      val project1 = ProjectDao.new { name = "Spring Boot" }
      val project2 = ProjectDao.new { name = "Scala" }

      project1.developers = SizedCollection(listOf(dev1, dev2, dev3))
      project2.developers = SizedCollection(listOf(dev2))
    }
  }

}
