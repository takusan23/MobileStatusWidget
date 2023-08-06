// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application").version("8.1.0").apply(false)
    id("com.android.library").version("8.1.0").apply(false)
    id("org.jetbrains.kotlin.android").version("1.8.21").apply(false)
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