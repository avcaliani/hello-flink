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
    implementation("com.google.guava:guava:32.1.2-jre") // Example runtime dependency
    compileOnly("org.slf4j:slf4j-api:2.0.9") // "Provided" dependency
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0") // Example Test Dependency
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
            !it.name.contains("slf4j-api")
        }.map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    with(tasks.jar.get() as CopySpec)
}