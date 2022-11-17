plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.codequalitytools)
  application
}

rootProject.configure<com.vanniktech.code.quality.tools.CodeQualityToolsPluginExtension> {
  checkstyle {
    enabled = false // Kotlin only.
  }
  pmd {
    enabled = false // Kotlin only.
  }
  ktlint {
    toolVersion = libs.versions.ktlint.get()
    experimental = true
  }
  detekt {
    enabled = false // Don't want this.
  }
  cpd {
    enabled = false // Kotlin only.
  }
  lint {
    checkAllWarnings = true
  }
}

repositories {
  mavenCentral()
  mavenLocal()
}

application {
  applicationName = "music"
  mainClass.set("com.vanniktech.music.MainKt")
}

defaultTasks("run")

kotlin {
  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(18))
  }
}

dependencies {
  implementation(libs.kotlinx.datetime)
}

dependencies {
  testImplementation(libs.kotlin.test.junit)
}
