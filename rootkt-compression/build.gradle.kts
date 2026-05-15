plugins {
    kotlin("jvm")
    `maven-publish`
    signing
    java
    id("com.gradleup.nmcp")
}

group = "io.github.thisismeamir"
version = project.property("version") as String

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    implementation("org.lz4:lz4-java:1.8.0")
    implementation("com.github.luben:zstd-jni:1.5.5-11")
    // lzma — pure Java, no native dep needed
    implementation("org.tukaani:xz:1.9")
    // zlib — already in JDK via java.util.zip, no dep needed
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = project.group.toString()
            artifactId = "rootkt-compression"
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

