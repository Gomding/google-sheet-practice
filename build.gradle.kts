import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.noarg") version "1.5.31"
    kotlin("jvm") version "1.7.21"
    kotlin("plugin.spring") version "1.5.31"
    kotlin("plugin.jpa") version "1.5.31"
    application
}

group = "com.member"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // web
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // https://mvnrepository.com/artifact/org.mindrot/jbcrypt
    // 네이버 인증 토큰 생성을 위한 전자서명을 생성할 때 사용
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    // persistence
    runtimeOnly("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // google-sheet-client
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-sheets:v4-rev20220927-2.0.0")

    // https://mvnrepository.com/artifact/com.google.auth/google-auth-library-oauth2-http
    implementation("com.google.auth:google-auth-library-oauth2-http:1.16.0")

    // object mapping
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")

    // https://mvnrepository.com/artifact/com.konghq/unirest-java
    implementation("com.konghq:unirest-java:3.14.2")

    // test
    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter", "junit-jupiter", "5.8.2")
    testImplementation("io.kotest", "kotest-runner-junit5", "5.3.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

tasks.jar {
    enabled = false
}

tasks.bootJar {
    enabled = true
    archiveFileName.set("google-sheet-practice.jar")
    mainClass.set("com.google.sheet.practice.GoogleSheetPracticeApplicationKt")
}