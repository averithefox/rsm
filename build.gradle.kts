plugins {
  `maven-publish`
  id("net.fabricmc.fabric-loom-remap")
  id("com.gradleup.shadow") version "9.4.1"
  id("com.github.jmongard.git-semver-plugin") version "0.18.0"
}

semver {
  groupVersionIncrements = false
}

val modVersion: String by project
val mavenGroup: String by project
val minecraftVersion: String by project
val loaderVersion: String by project
val fabricApiVersion: String by project
val lwjglVersion: String by project

version = semver.infoVersion
group = "com.ricedotwho"

val shadowApi by configurations.creating {
  configurations.api {
    extendsFrom(this@creating)
  }
}

dependencies {
  minecraft("com.mojang:minecraft:$minecraftVersion")
  mappings(loom.officialMojangMappings())
  modImplementation("net.fabricmc:fabric-loader:$loaderVersion")

  modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

  shadowApi("org.lwjgl:lwjgl-nanovg:${lwjglVersion}")
  listOf("windows", "linux", "macos", "macos-arm64").forEach {
    shadowApi("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-$it")
  }

  annotationProcessor("org.projectlombok:lombok:1.18.32")
  compileOnly("org.projectlombok:lombok:1.18.32")
}

tasks {
  withType<JavaCompile>().configureEach {
    options.release = 21
  }

  val expandProps = project.properties
  processResources {
    filesMatching("fabric.mod.json") {
      expand(expandProps)
    }
  }

  jar {
    archiveClassifier = "nodeps"
    destinationDirectory = layout.buildDirectory.dir("badjars")
  }

  shadowJar {
    archiveClassifier = "dev-shadow"
    destinationDirectory = layout.buildDirectory.dir("badjars")

    configurations = listOf(shadowApi)

    minimize()

    exclude("META-INF/maven/")
  }

  remapJar {
    archiveClassifier = null
    from(shadowJar)
    inputFile = shadowJar.get().archiveFile
  }
}

java {
  withSourcesJar()
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      artifact(tasks.remapJar)
      artifact(tasks.remapSourcesJar)
    }
  }

  repositories {
    mavenLocal()
  }
}
