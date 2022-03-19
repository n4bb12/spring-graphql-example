package dev.n4bb12.spring.graphql.example.projects.jpa

import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.data.repository.CrudRepository
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.ManyToMany

@Entity
data class Developer(
  @Id
  val id: String,
  val firstName: String,
  val lastName: String,
)

@Entity
data class Project(
  @Id
  val id: String,
  val name: String,
  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  val developers: List<Developer>,
)

interface DevelopersRepository : CrudRepository<Developer, String>
interface ProjectsRepository : CrudRepository<Project, String>
