package dev.n4bb12.spring.graphql.example.projects.jpa

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface HqlProjectsRepository : CrudRepository<Project, String> {

  @Query("SELECT DISTINCT p FROM Project p LEFT JOIN FETCH p.developers")
  fun findAllWithDevelopersUsingHQL(): Iterable<Project>

}
