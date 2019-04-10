import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  java
  kotlin("jvm") version "1.3.21"
  kotlin("plugin.spring") version "1.3.21"
  id("io.franzbecker.gradle-lombok") version "2.1"
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

  implementation("io.vavr:vavr:0.10.0")
  implementation("org.slf4j:slf4j-api:1.7.26")
  implementation("ch.qos.logback:logback-classic:1.2.3")
  annotationProcessor("org.projectlombok:lombok:$lombokVersion")

  testImplementation("com.codeborne:selenide:5.2.2")
  testImplementation(platform("org.testcontainers:testcontainers-bom:1.11.1"))
  testImplementation("org.testcontainers:selenium")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation(platform("org.junit:junit-bom:5.5.0-M1"))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntime("org.junit.platform:junit-platform-launcher")
  testRuntimeOnly("org.junit.vintage:junit-vintage-engine")
  testImplementation("junit:junit:4.12")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.13")
  testImplementation("org.assertj:assertj-core:3.12.2")
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
}

defaultTasks("clean", "sources", "fatJar", "installDist")
