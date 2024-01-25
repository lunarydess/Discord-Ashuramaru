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
    // group, name, version, shadow, <optional: exclusions>
    val nodeps = emptyArray<String>()
    val exclJavaCord = arrayOf(
        "com.squareup",
        "com.codahale",
        "com.neovisionaries",
        "io.vavr",
        "org.bouncycastle",
        "com.fasterxml.jackson"
    )
    arrayOf( // @formatter:off
        arrayOf("org.jetbrains.kotlin", "kotlin-stdlib",      "2.0.0-Beta3" , true , nodeps),
        arrayOf("org.jetbrains"       , "annotations"  , prop("ver_ann-jbr"), false, nodeps),
        
        arrayOf("info.picocli" , "picocli"    , prop("ver_picocli") , true , nodeps),

        /* javacord dependency hell - begin */
        arrayOf("org.javacord", "javacord-api" , prop("ver_javacord"), true, exclJavaCord),
        arrayOf("org.javacord", "javacord-core", prop("ver_javacord"), true, exclJavaCord),

        arrayOf("com.squareup.okio"   , "okio-jvm"           , prop("ver_okio"  ), true, nodeps),
        arrayOf("com.squareup.okhttp3", "okhttp"             , prop("ver_okhttp"), true, nodeps),
        arrayOf("com.squareup.okhttp3", "logging-interceptor", prop("ver_okhttp"), true, nodeps),

        arrayOf("com.codahale"        , "xsalsa20poly1305"   , prop("ver_xsalsa"), true, nodeps),
        arrayOf("com.neovisionaries"  , "nv-websocket-client", prop("ver_nvwebs"), true, nodeps),

        arrayOf("io.vavr"             , "vavr"               , prop("ver_vavr"  ), true, nodeps),
        arrayOf("io.vavr"             , "vavr-match"         , prop("ver_vavr"  ), true, nodeps),
        
        arrayOf("org.bouncycastle"    , "bcprov-jdk18on"     , prop("ver_bcprov"), true, nodeps),

        arrayOf("com.fasterxml.jackson.core", "jackson-annotations", prop("ver_jackson"), true, nodeps),
        arrayOf("com.fasterxml.jackson.core", "jackson-core"       , prop("ver_jackson"), true, nodeps),
        arrayOf("com.fasterxml.jackson.core", "jackson-databind"   , prop("ver_jackson"), true, nodeps),
        /* javacord dependency hell - end (me) */
        
        // implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0-Beta3")
        
        // ugliest workaround EVER ;-;
        arrayOf("org.slf4j"               , "slf4j-api"        , prop("ver_slf4j"  ), true, nodeps),
        arrayOf("org.apache.logging.log4j", "log4j-api"        , prop("ver_log4j"  ), true, nodeps),
        arrayOf("org.apache.logging.log4j", "log4j-slf4j2-impl", prop("ver_log4j"  ), true, nodeps),
        arrayOf("org.tinylog"             , "tinylog-api"      , prop("ver_tinylog"), true, nodeps),
        arrayOf("org.tinylog"             , "slf4j-tinylog"    , prop("ver_tinylog"), true, nodeps),
        arrayOf("org.tinylog"             , "tinylog-impl"     , prop("ver_tinylog"), true, nodeps),
    ).forEach {
        if(it[3] as Boolean)
        shadow(group = it[0].toString(), name = it[1].toString(), version = it[2].toString()) {
            (it[4] as Array<String>).forEach {dep -> exclude(group = dep) }
        }
        implementation (group = it[0].toString(), name = it[1].toString(), version = it[2].toString()) {
            (it[4] as Array<String>).forEach {dep -> exclude(group = dep) }
        }
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
    shadow        (dependencyNotation =             localShadowDeps)
    implementation(dependencyNotation = localDeps + localShadowDeps)
    // @formatter:on
}

tasks.withType<Jar> { // @formatter:off
    manifest.attributes(mapOf(
        "Manifest-Version" to "1.0",
        "Main-Class"       to "rip.lunarydess.ashuramaru.Ashuramaru",
    ))
} // @formatter:on

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
