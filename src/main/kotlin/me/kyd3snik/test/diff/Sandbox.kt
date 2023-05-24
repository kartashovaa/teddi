package me.kyd3snik.test.diff

import org.gradle.tooling.GradleConnector
import org.gradle.tooling.model.idea.IdeaProject
import java.io.File


fun main(args: Array<String>) {
    // Configure the connector and create the connection
    val connector = GradleConnector.newConnector()
    if (args.isNotEmpty()) {
        connector.useInstallation(File(args[0]))
        if (args.size > 1) {
            connector.useGradleUserHomeDir(File(args[1]))
        }
    }
    connector.forProjectDirectory(File("."))
    val connection = connector.connect()
    connection.use { connection ->
        val project = connection.getModel(IdeaProject::class.java)
        println("***")
        println("Project details: ")
        println(project)
        println("***")
        println("Project modules: ")
        for (module in project.modules) {
            println("  $module")
            println("  module details:")
            println("    tasks from associated gradle project:")
            for (task in module.gradleProject.tasks) {
                println("      " + task.name)
            }
            for (root in module.contentRoots) {
                println("    Content root: " + root.rootDirectory)
                println("    source dirs:")
                for (dir in root.sourceDirectories) {
                    println("      $dir")
                }
                println("    test dirs:")
                for (dir in root.testDirectories) {
                    println("      $dir")
                }
                println("    exclude dirs:")
                for (dir in root.excludeDirectories) {
                    println("      $dir")
                }
            }
            println("    dependencies:")
            for (dependency in module.dependencies) {
                println("      * $dependency")
            }
        }
    }
}