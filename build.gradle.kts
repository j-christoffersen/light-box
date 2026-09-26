plugins {
    id("application")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")
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

tasks.named<JavaExec>("run") {}

// note: this is currently not working due to a bug loading resource files from the build .jar
tasks.register<Exec>("deploy") {
    dependsOn("installDist")
    commandLine("rsync", "-avz", "./build/install/light-box/", "lightbox:~/code/light-box-build")
}

// TODO use sudo
// tasks.register<Exec>("runPi") {
//     commandLine("ssh", "lightbox", "\"ls && cd ~/code/light-box-build && ls && ./bin/light-box\"")
// }

tasks.register<Exec>("deployFull") {
    commandLine("rsync", "-avz", ".", "lightbox:~/code/light-box-sync")
}

// TODO use sudo
// tasks.register<Exec>("runPiFull") {
//     commandLine("ssh", "lightbox", "\"cd /home/jackson/code/light-box-sync & && ./bin/light-box\"")
// }

// TOOLS

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

tasks.register<JavaExec>("surflineApi") {
    group = "application"
    description = "Call the Surfline API and print the data"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.jchristoffersen.lightbox.tools.SurflineApiTool")
}
