package dev.n4bb12.spring.graphql.example.projects

import dev.n4bb12.spring.graphql.example.projects.jpa.DevelopersRepository
import dev.n4bb12.spring.graphql.example.projects.jpa.ProjectsRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProjectsRestController(
  private val developersRepository: DevelopersRepository,
  private val projectsRepository: ProjectsRepository
) {

  @GetMapping("/developers")
  fun developers() = developersRepository.findAll()

  @GetMapping("/projects")
  fun projects() = projectsRepository.findAll()

}
