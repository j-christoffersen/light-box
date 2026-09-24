plugins {
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.google.guava:guava:33.3.1-jre")
    implementation("com.socketio4j:netty-socketio-core:4.0.1")
    implementation("net.java.dev.jna:jna:5.14.0")
}

application {
    mainClass.set("dev.jchristoffersen.lightbox.Main")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

tasks.named<JavaExec>("run") {
    jvmArgs=listOf("-Djna.library.path=~/code/rpi-rgb-led-matrix/lib/ilibrgbmatrix.so.1")
}

tasks.register<JavaExec>("runLocal") {
    group = "application"
    description = "Executes the Local.java entrypoint file."
    
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.jchristoffersen.lightbox.Local") 
}

tasks.register<JavaExec>("parseBdf") {
    group = "application"
    description = "Parse a BDF and inspect the glyphs"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.jchristoffersen.lightbox.tools.ParseBdf")
}

tasks.register<JavaExec>("textRenderer") {
    group = "application"
    description = "Render text to a bitmap and print to console"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.jchristoffersen.lightbox.tools.TextRendererTool")
}
