import com.netflix.graphql.dgs.codegen.gradle.GenerateJavaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("org.springframework.boot") version "2.7.0-M2"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.10"
  kotlin("plugin.spring") version "1.6.10"
  kotlin("plugin.jpa") version "1.6.10"
  kotlin("kapt") version "1.6.10"
  id("com.apollographql.apollo3") version "3.1.0"
  id("com.netflix.dgs.codegen") version "5.1.16"
}

group = "dev.n4bb12"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val exposedVersion = "0.37.3"

repositories {
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
  // Kotlin
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  // Spring MVC
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-security")

  // GraphQL
  implementation("org.springframework.boot:spring-boot-starter-graphql")
  implementation("com.graphql-java-kickstart:playground-spring-boot-starter:11.1.0")
  implementation(platform("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release")) // DGS
  implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter") // DGS
  implementation("com.apollographql.apollo3:apollo-runtime:3.1.0") // Apollo Client

  // Persistence
  runtimeOnly("com.h2database:h2") // H2
  implementation("org.springframework.boot:spring-boot-starter-data-jpa") // JPA
  kapt("org.hibernate:hibernate-jpamodelgen:5.6.7.Final") // Criteria
  implementation("org.springframework.boot:spring-boot-starter-jooq") // jOOQ
  implementation("org.jetbrains.exposed:exposed-core:$exposedVersion") // Exposed
  implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion") // Exposed
  implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion") // Exposed

  // Testing
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.springframework.graphql:spring-graphql-test")

  // DX
  developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "17"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.withType<GenerateJavaTask> {
  schemaPaths = mutableListOf("src/main/resources/graphql")
  typeMapping = mutableMapOf(
    "Coordinates" to "dev.n4bb12.spring.graphql.example.vehicle.Coordinates",
  )
  packageName = "dev.n4bb12.spring.graphql.example.dgs"
  generateClient = true
  generateDataTypes = true
}

apollo {
  packageName.set("dev.n4bb12.spring.graphql.example.client")
}
