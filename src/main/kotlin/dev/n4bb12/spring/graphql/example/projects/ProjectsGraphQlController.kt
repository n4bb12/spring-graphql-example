package dev.n4bb12.spring.graphql.example.projects

import dev.n4bb12.spring.graphql.example.projects.exposed.ExposedProjectsRepository
import dev.n4bb12.spring.graphql.example.projects.jooq.JooqProjectsRepository
import dev.n4bb12.spring.graphql.example.projects.jpa.*
import graphql.schema.DataFetchingEnvironment
import org.hibernate.Hibernate
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller
import javax.transaction.Transactional

@Controller
class DevelopersGraphQlController(
  private val repository: DevelopersRepository,
) {

  @QueryMapping
  fun developers() = repository.findAll()

}

/**
 * Fetch using DataFetchingEnvironment + Hibernate.initialize --> n + 1 query
 */
//@Controller
class ProjectsGraphQlControllerV1(
  private val repository: ProjectsRepository
) {

  @Transactional
  @QueryMapping
  fun projects(fetchingEnv: DataFetchingEnvironment): List<Project> {
    val projects = repository.findAll()
    val fetchDevs = fetchingEnv.selectionSet.contains("Project.developers")

    println(fetchingEnv.selectionSet.fields)

    if (fetchDevs) {
      projects.forEach { Hibernate.initialize(it.developers) }
    }

    return projects.toList()
  }

}

/**
 * Fetch using field resolver + Hibernate.initialize --> n + 1 query
 */
@Controller
class ProjectsGraphQlControllerV2(
  private val repository: ProjectsRepository
) {

  @QueryMapping
  fun projects() = repository.findAll()

  @Transactional
  @SchemaMapping
  fun developers(project: Project): List<Developer> {
    val projectWithDevs = repository.findById(project.id).get()
    Hibernate.initialize(projectWithDevs.developers)
    return projectWithDevs.developers
  }

}

/**
 * Fetch using DataFetchingEnvironment + custom HQL --> 1 query
 */
//@Controller
class ProjectsGraphQlControllerV3(
  private val repository: HqlProjectsRepository
) {

  @QueryMapping
  fun projects(fetchingEnv: DataFetchingEnvironment): Iterable<Project> {
    val fetchDevs = fetchingEnv.selectionSet.contains("Project.developers")

    return if (fetchDevs) {
      repository.findAllWithDevelopersUsingHQL()
    } else {
      repository.findAll()
    }
  }

}

/**
 * Fetch using DataFetchingEnvironment + Criteria --> 1 query
 */
//@Controller
class ProjectsGraphQlControllerV4(
  private val repository: CriteriaProjectsRepository,
) {

  @QueryMapping
  fun projects(fetchingEnv: DataFetchingEnvironment): Iterable<Project> {
    val fetchDevs = fetchingEnv.selectionSet.contains("Project.developers")

    return if (fetchDevs) {
      repository.findAllWithDevelopersUsingCriteria()
    } else {
      repository.findAll()
    }
  }

}

/**
 * Fetch using jOOQ --> TODO
 */
//@Controller
class ProjectsGraphQlControllerV5(
  private val repository: JooqProjectsRepository,
) {

  @QueryMapping
  fun projects(fetchingEnv: DataFetchingEnvironment): Iterable<Project> {
    return repository.findAll(fetchingEnv)
  }

}

/**
 * Fetch using Exposed DAO --> 1 + 1 query
 */
//@Controller
class ProjectsGraphQlControllerV6(
  private val repository: ExposedProjectsRepository,
) {

  @QueryMapping
  fun projects(fetchingEnv: DataFetchingEnvironment): Iterable<Project> {
    return repository.findAll(fetchingEnv)
  }

}


