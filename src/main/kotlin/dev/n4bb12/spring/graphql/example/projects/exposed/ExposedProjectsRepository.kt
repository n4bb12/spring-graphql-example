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

object ExposedDevelopersTable : UUIDTable() {
  val firstName = varchar("firstName", 50)
  val lastName = varchar("lastName", 50)
}

object ExposedProjectsTable : UUIDTable() {
  val name = varchar("name", 50)
}

object ExposedProjectDevelopersJoinTable : Table() {
  val developer = reference("developer", ExposedDevelopersTable)
  val project = reference("project", ExposedProjectsTable)

  override val primaryKey = PrimaryKey(developer, project)
}

class DeveloperDao(id: EntityID<UUID>) : UUIDEntity(id) {
  companion object : UUIDEntityClass<DeveloperDao>(ExposedDevelopersTable)

  var firstName by ExposedDevelopersTable.firstName
  var lastName by ExposedDevelopersTable.lastName
}

class ProjectDao(id: EntityID<UUID>) : UUIDEntity(id) {
  companion object : UUIDEntityClass<ProjectDao>(ExposedProjectsTable)

  var name by ExposedProjectsTable.name
  var developers by DeveloperDao via ExposedProjectDevelopersJoinTable
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
