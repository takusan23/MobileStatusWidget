// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    /** Kotlinのバージョン */
    val kotlinVersion by extra("1.5.31")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean") {
    println("run clean !!!!!")
    doFirst {
        delete(rootProject.buildDir)
    }
}

tasks.register("exportDependency") {
    doFirst {
        // テキストファイル保存先
        val libraryListFile = File(rootDir, "libraryList.txt")
        libraryListFile.createNewFile()
        // 書き込む文字列
        val libraryListText = project("app")
            .configurations["implementationDependenciesMetadata"]
            .resolvedConfiguration
            .firstLevelModuleDependencies
            .joinToString(separator = "\n-----\n") {
                """
                name = ${it.name}
                version = ${it.moduleVersion}
                """.trimIndent()
            }
        // 書き込む
        libraryListFile.writeText(libraryListText) // Kotlinの拡張関数も呼べる！？
    }
}