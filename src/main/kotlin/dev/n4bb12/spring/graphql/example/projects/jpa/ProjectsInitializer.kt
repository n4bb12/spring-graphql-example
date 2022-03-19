package dev.n4bb12.spring.graphql.example.projects.jpa

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class ProjectsInitializer(
  private val developersRepository: DevelopersRepository, private val projectsRepository: ProjectsRepository
) : CommandLineRunner {

  override fun run(vararg args: String?) {
    val dev1 = Developer("1", "Josh", "Long")
    val dev2 = Developer("2", "James", "Ward")
    val dev3 = Developer("3", "Oliver", "Gierke")

    val developers = listOf(dev1, dev2, dev3)
    developersRepository.saveAll(developers)

    val project1 = Project("1", "Spring Boot", developers)
    val project2 = Project("2", "Scala", listOf(dev2))

    val projects = listOf(project1, project2)
    projectsRepository.saveAll(projects)
  }

}
