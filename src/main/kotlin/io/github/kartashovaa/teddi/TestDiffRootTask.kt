package io.github.kartashovaa.teddi

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

abstract class TestDiffRootTask : DefaultTask() {

    companion object {

        fun register(project: Project): TaskProvider<TestDiffRootTask> {
            require(project === project.rootProject)
            return project.tasks.register("testDiffUnitTest", TestDiffRootTask::class.java) { rootTask ->
                project.subprojects { childProject ->
                    childProject.tasks.withType(TestDiffTask::class.java).all { task ->
                        rootTask.dependsOn(task)
                    }
                }
            }
        }
    }
}