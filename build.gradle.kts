import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.nio.charset.StandardCharsets

plugins {
    id("idea")

    id("java")
    id("java-library")

    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.jetbrains.kotlin.jvm") version "2.0.0-Beta3"
}

group = "rip.lunarydess"
version = "0.0.0-dev"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

    // maven("https://jitpack.io") TODO: ... | fix Lilith not being able to be built on jitpack D:
    flatDir { dirs("libs") }    // TODO: ... | remove local repos after fixing Lilith's publishing
}

fun <V> prop(value: String): V = properties[value] as V
dependencies {
    arrayOf( // @formatter:off
        arrayOf("org.jetbrains", "annotations", prop("ver_ann-jbr") , false),

        arrayOf("com.akuleshov7" , "ktoml-file-jvm" , prop("ver_ktoml")   , true ),
        arrayOf("org.javacord"   , "javacord"       , prop("ver_javacord"), true ),
        arrayOf("info.picocli"   , "picocli"        , prop("ver_picocli") , true ),
        
        arrayOf("org.slf4j"  , "slf4j-api"    , prop("ver_slf4j"  ), true),
        arrayOf("org.tinylog", "slf4j-tinylog", prop("ver_tinylog"), true)
    ).forEach {
        if(it[3] as Boolean)
        shadow      (group = it[0].toString(), name = it[1].toString(), version = it[2].toString())
        compileOnly (group = it[0].toString(), name = it[1].toString(), version = it[2].toString())
    } // @formatter:on

    val localDeps = fileTree(
        mapOf(
            "dir" to "libs/exist",
            "include" to listOf("*.jar"),
            "includes" to listOf("**")
        )
    )
    val localShadowDeps = fileTree(
        mapOf(
            "dir" to "libs/shadow",
            "include" to listOf("*.jar"),
            "includes" to listOf("**")
        )
    )
    // @formatter:off
    shadow     (dependencyNotation =             localShadowDeps)
    compileOnly(dependencyNotation = localDeps + localShadowDeps)
    // @formatter:on
}

tasks.withType<AbstractArchiveTask> {
    isReproducibleFileOrder = true
    isPreserveFileTimestamps = false
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
    options.encoding = StandardCharsets.UTF_8.toString()
}

tasks.withType<ShadowJar> {
    configurations = listOf(project.configurations.shadow.get())
    isZip64 = true
}

configurations.shadow { isTransitive = false }
