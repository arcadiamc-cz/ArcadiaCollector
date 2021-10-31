plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "cz.speedy"
version = "1.0"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks{
    shadowJar {
        baseName = "mccollector"
        classifier = null
        version = null
        manifest {
            attributes(Pair("Main-Class", "cz.speedy.mccollector.McCollector"))
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.influxdb:influxdb-java:2.22")
    implementation("com.google.code.gson:gson:2.8.8")
}