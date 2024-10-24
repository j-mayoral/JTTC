plugins {
    id("java")
    id ("org.javacc.javacc") version "3.0.2"
    id ("application")
}

group = "org.fixpoint"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass = "org.turing2javagenerics.Main"
}

tasks.test {
    useJUnitPlatform()
}

tasks.compileJavacc {
    inputDirectory = file("src/main/javacc")
    outputDirectory = file("src/main/java/org/generated/")
}
