plugins {
    kotlin("multiplatform") version "2.1.20"
}

group = "com.uexodus"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    linuxX64("karchinstall") {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
        compilations.getByName("main") {
            cinterops {
                val libparted by creating
            }
        }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "8.10"
    distributionType = Wrapper.DistributionType.BIN
}


