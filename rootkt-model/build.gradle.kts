plugins {
    kotlin("jvm")
    `maven-publish`
    signing
    java
    id("com.gradleup.nmcp")
}

group = "io.github.thisismeamir"
version =  project.property("version") as String

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":rootkt-compression"))
    implementation(project(":rootkt-streamer"))
    implementation(project(":rootkt-format"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "rootkt-model"
            version = project.version.toString()

            pom {
                name.set("rootkt")
                description.set("Kotlin library for reading and writing ROOT files.")
                url.set("https://github.com/thisismeamir/root.kt")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("thisismeamir")
                        name.set("Kid A")
                    }
                }

                scm {
                    url.set("https://github.com/thisismeamir/root.kt")
                    connection.set("scm:git:git://github.com/thisismeamir/root.kt.git")
                    developerConnection.set("scm:git:ssh://github.com/thisismeamir/root.kt.git")
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("SIGNING_KEY"),
        System.getenv("SIGNING_PASSWORD")
    )
    sign(publishing.publications["mavenJava"])
}

