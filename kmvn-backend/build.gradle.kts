@file:Suppress("unused")

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

/*
 * Copyright © 2025 RTAkland
 * Date: 2025/4/13 22:42
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("cn.rtast.kembeddable") version "1.2.4"
}

repositories {
    mavenCentral()
    maven("https://repo.maven.rtast.cn/releases")
}

kotlin {
    linuxX64 {
        compilations["main"].cinterops {
            val fileTimeLinux by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/file_time_linuxx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
        }
    }
    mingwX64 {
        compilations["main"].cinterops {
            val fileTimeMingw by creating {
                definitionFile = project.layout.projectDirectory.dir("src/cinterop/file_time_mingwx64.def").asFile
                compilerOpts("-Isrc/cinterop/")
            }
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        binaries.executable {
            entryPoint = "cn.rtast.kmvnrepo.main"
        }
        compilerOptions.freeCompilerArgs.add("-Xallocator=std")
    }

    val ktorVersion = "3.1.2"

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kmvn-common"))
            implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.7.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("io.ktor:ktor-server-auto-head-response:${ktorVersion}")
            implementation("io.ktor:ktor-server-core:$ktorVersion")
            implementation("io.ktor:ktor-server-cio:$ktorVersion")
            implementation("io.ktor:ktor-server-auth:$ktorVersion")
            implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            implementation("io.ktor:ktor-server-cors:$ktorVersion")
            implementation("io.ktor:ktor-client-cio:${ktorVersion}")
            implementation("io.ktor:ktor-client-core:${ktorVersion}")
            implementation("io.github.pdvrieze.xmlutil:core:0.90.3")
            implementation("io.github.pdvrieze.xmlutil:serialization:0.90.3")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

kembeddable {
    compression = true
    resourcePath.add("nativeMain/resources")
    packageName = "cn.rtast.kmvnrepo.resources"
}

tasks.register("deployBackend") {
    dependsOn(tasks.named("linkReleaseExecutableLinuxX64"))
    doLast {
        exec {
            commandLine(
                "scp",
                "build/bin/linuxX64/releaseExecutable/backend.kexe",
                "root@lan.rtast.cn:/tmp/backend.kexe"
            )
            isIgnoreExitValue = true
        }
        exec {
            commandLine("ssh", "root@lan.rtast.cn", "rm /root/reposilite/backend.kexe")
            isIgnoreExitValue = true
        }
        exec {
            commandLine("ssh", "root@lan.rtast.cn", "mv /tmp/backend.kexe /root/reposilite")
            isIgnoreExitValue = true
        }
        exec {
            commandLine("ssh", "root@lan.rtast.cn", "chmod +x /root/reposilite/backend.kexe")
            isIgnoreExitValue = true
        }
        exec {
            commandLine("ssh", "root@lan.rtast.cn", "systemctl restart reposilite.service")
            isIgnoreExitValue = true
        }
    }
}