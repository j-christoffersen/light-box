plugins {
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("net.java.dev.jna:jna:5.14.0")
}

application {
    // Change to your actual main class once you create it
    mainClass = "dev.jchristoffersen.lightbox.Main"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named("run") {
    // Uncomment if the native library needs a specific search path
    // jvmArgs += ["-Djna.library.path=/path/to/your/so/files"]
}
