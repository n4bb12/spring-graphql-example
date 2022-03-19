package dev.n4bb12.spring.graphql.example.projects.exposed

import dev.n4bb12.spring.graphql.example.projects.jpa.Developer
import dev.n4bb12.spring.graphql.example.projects.jpa.Project
import graphql.schema.DataFetchingEnvironment
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.dao.with
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.util.*
import javax.sql.DataSource

object DevelopersTable : UUIDTable() {
  val firstName = varchar("firstName", 50)
  val lastName = varchar("lastName", 50)
}

object ProjectsTable : UUIDTable() {
  val name = varchar("name", 50)
}

object ProjectDevelopersJoinTable : Table() {
  val developer = reference("developer", DevelopersTable)
  val project = reference("project", ProjectsTable)

  override val primaryKey = PrimaryKey(developer, project)
}

class DeveloperDao(id: EntityID<UUID>) : UUIDEntity(id) {
  companion object : UUIDEntityClass<DeveloperDao>(DevelopersTable)

  var firstName by DevelopersTable.firstName
  var lastName by DevelopersTable.lastName
}

class ProjectDao(id: EntityID<UUID>) : UUIDEntity(id) {
  companion object : UUIDEntityClass<ProjectDao>(ProjectsTable)

  var name by ProjectsTable.name
  var developers by DeveloperDao via ProjectDevelopersJoinTable
}

@Component
class ExposedProjectsRepository(private val dataSource: DataSource) {

  fun findAll(fetchingEnv: DataFetchingEnvironment): Collection<Project> {
    Database.connect(dataSource)

    return transaction {
      addLogger(StdOutSqlLogger)

      val fetchDevs = fetchingEnv.selectionSet.contains("Project.developers")

      if (fetchDevs) {
        // 1 + 1 query
        ProjectDao.all().with(ProjectDao::developers).map { mapProjectWithDevelopers(it) }
      } else {
        // 1 query
        ProjectDao.all().map { mapProject(it) }
      }
    }
  }

  fun mapDeveloper(developer: DeveloperDao) =
    Developer(developer.id.toString(), developer.firstName, developer.lastName)

  fun mapProject(project: ProjectDao) = Project(project.id.toString(), project.name, emptyList())

  fun mapProjectWithDevelopers(project: ProjectDao) =
    Project(project.id.toString(), project.name, project.developers.map(this::mapDeveloper))

}
