import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.example"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val vertxVersion = "4.0.0"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "cc.benhull.simpleblog.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
    mainClassName = launcherClassName
}

dependencies {
    annotationProcessor("io.vertx:vertx-codegen:4.0.2:processor")
    annotationProcessor("io.vertx:vertx-web-api-service:4.0.2")
    annotationProcessor("io.vertx:vertx-codegen:4.0.2:processor")
    annotationProcessor("io.vertx:vertx-service-proxy:4.0.2")
    implementation("io.vertx:vertx-service-proxy:4.0.2")
    implementation("io.vertx:vertx-web-api-service:4.0.2")
    implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
    implementation("io.vertx:vertx-web-client")
    implementation("io.vertx:vertx-rx-java2")
    implementation("io.vertx:vertx-web")
    implementation("io.vertx:vertx-pg-client")
    testImplementation("io.vertx:vertx-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("fat")
    manifest {
        attributes(mapOf("Main-Verticle" to mainVerticleName))
    }
    mergeServiceFiles()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events = setOf(PASSED, SKIPPED, FAILED)
    }
}


tasks.withType<JavaExec> {
    args = listOf(
        "run",
        mainVerticleName,
        "--redeploy=$watchForChange",
        "--launcher-class=$launcherClassName",
        "--on-redeploy=$doOnChange"
    )
    systemProperties["vertx.logger-delegate-factory-class-name"] =
        "io.vertx.core.logging.SLF4JLogDelegateFactory"
}
