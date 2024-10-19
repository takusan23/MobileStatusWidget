// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
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