package dev.n4bb12.spring.graphql.example.projects.jpa

import org.springframework.data.repository.CrudRepository
import javax.persistence.EntityManager

interface CriteriaQueries {
  fun findAllWithDevelopersUsingCriteria(): Iterable<Project>
}

class CriteriaQueriesImpl(private val entityManager: EntityManager) : CriteriaQueries {

  override fun findAllWithDevelopersUsingCriteria(): Iterable<Project> {
    val query = entityManager.criteriaBuilder.createQuery(Project::class.java)
    val project = query.from(Project::class.java)

    project.fetch(Project_.developers)
    query.select(project).distinct(true)

    return entityManager.createQuery(query).resultList
  }

}

interface CriteriaProjectsRepository : CrudRepository<Project, String>, CriteriaQueries
