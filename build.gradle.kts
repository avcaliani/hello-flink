plugins {
    application
    java
}

group = "br.avcaliani"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("br.avcaliani.hello_flink.App")
}

repositories {
    mavenCentral()
}

dependencies {

    // Provided Dependency
    compileOnly("org.apache.flink:flink-streaming-java:2.0.0")
    compileOnly("org.apache.flink:flink-csv:2.0.0")
    compileOnly("org.apache.flink:flink-connector-files:2.0.0")

    // Lombok
    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.register<Jar>("uberJar") {
    dependsOn(tasks.jar)
    archiveClassifier.set("uber")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    from(
        configurations.runtimeClasspath.get().filter {
            // Exclude the "provided" dependency
            !it.name.contains("flink")
        }.map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    with(tasks.jar.get() as CopySpec)
}