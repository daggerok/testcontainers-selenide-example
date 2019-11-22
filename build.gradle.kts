import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  kotlin("jvm") version "1.3.60"
  kotlin("plugin.spring") version "1.3.60"
  id("io.franzbecker.gradle-lombok") version "3.2.0"
  id("com.github.ben-manes.versions") version "0.27.0"
}

tasks.withType(Wrapper::class.java) {
  gradleVersion = "5.4-rc-1"
  distributionType = Wrapper.DistributionType.BIN
}

sourceSets {
  main {
    java.srcDir("src/main/kotlin")
  }
  test {
    java.srcDir("src/test/kotlin")
  }
}

val javaVersion = JavaVersion.VERSION_1_8

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "$javaVersion"
  }
}

java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
}

repositories {
  mavenCentral()
}

val lombokVersion: String by project

lombok {
  version = lombokVersion
}

dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("reflect"))

  implementation("io.vavr:vavr:0.10.2")
  implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
  implementation("ch.qos.logback:logback-classic:1.3.0-alpha5")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")

  // testImplementation("com.codeborne:selenide:5.5.0")
  testImplementation("com.codeborne:selenide:5.2.8") // 5.3.+ doesn't worked...
  testImplementation(platform("org.testcontainers:testcontainers-bom:1.12.3"))
  testImplementation("org.testcontainers:selenium")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation(platform("org.junit:junit-bom:5.6.0-M1"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntime("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
  testImplementation("junit:junit:4.13-rc-1")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")
  testImplementation("org.assertj:assertj-core:3.14.0")
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    showExceptions = true
    showStandardStreams = true
    events(PASSED, SKIPPED, FAILED)
  }
}

tasks {
  named("clean") {
    doLast {
      delete(
          project.buildDir,
          "${project.projectDir}/out"
      )
    }
  }

  named<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
      componentSelection {
        all {
          // val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea", "M1", "BUILD-SNAPSHOT", "SNAPSHOT")
          val rejected = listOf("alpha") // ch.qos.logback:logback-classic:1.3.0-alpha*, io.vavr:vavr:1.0.0-alpha-*
                  .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                  .any { it.matches(candidate.version) }
          if (rejected) reject("Release candidate")
        }
      }
    }
    outputFormatter = "plain" // "json"
  }
}

defaultTasks("clean", "test")
