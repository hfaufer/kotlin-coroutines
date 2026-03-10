import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Apply the org.jetbrains.kotlin.jvm plugin to add support for Kotlin on the JVM.
    // It is defined in 'libs.versions.toml'.
    alias(libs.plugins.kotlin.jvm)
    // Apply the application plugin to add support for building a CLI application in Java.
    id("application")
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testRuntimeOnly(libs.junit.jupiter.platform.launcher)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    // Also try the Kotlin test library.
    testImplementation(kotlin("test"))

    // The application uses these dependencies.
    implementation(libs.jackson.module.kotlin)
    implementation(libs.kotlinx.coroutines.core)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    applicationDefaultJvmArgs = listOf("-Djava.util.logging.config.file=config/logging.properties")
    // Define the main class for the application.
    // Use the following command line to choose which class to launch:
    //   ./gradlew run -Plaunch=Kt020_variables
    // See https://discuss.gradle.org/t/can-i-specify-which-main-method-to-run-from-the-command-line/44825
    if (hasProperty("launch")) {
        mainClass.set("de.hu.kotlin.coroutines.${property("launch")}Kt")
    } else {
        mainClass = "de.hu.kotlin.coroutines.Kt010_HelloCoroutineKt"
    }
}

tasks.named<Test>("test") {
    // Lazy configuration of JUnit Platform for unit tests.
    // `task { useJUnitPlatform() }` would be eager.
    useJUnitPlatform()
}

// Enable preview feature "when with guards".
val compileKotlin: KotlinCompile by tasks
compileKotlin.compilerOptions {
    freeCompilerArgs.set(listOf("-Xwhen-guards"))
}
