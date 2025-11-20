plugins {
    buildlogic.`kotlin-common-conventions-no-detekt`
    kotlin("jvm") version "2.2.0"
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(8)
}