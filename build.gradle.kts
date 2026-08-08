plugins {
    kotlin("jvm") version "2.3.20" apply false
    `maven-publish`
    signing
    id("com.gradleup.nmcp.aggregation") version "1.4.4"
}

version = project.property("version") as String

nmcpAggregation {
    centralPortal {
        username = System.getenv("SONATYPE_USERNAME")
        password = System.getenv("SONATYPE_PASSWORD")
        publishingType = "AUTOMATIC"
    }
}

dependencies {
    nmcpAggregation(project(":rootkt-format"))
    nmcpAggregation(project(":rootkt-compression"))
    nmcpAggregation(project(":rootkt-streamer"))
    nmcpAggregation(project(":rootkt-model"))

}

allprojects {
    repositories {
        mavenCentral()
    }
}