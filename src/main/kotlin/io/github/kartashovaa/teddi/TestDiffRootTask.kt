package io.github.kartashovaa.teddi

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider

abstract class TestDiffRootTask : DefaultTask(), OptionsFacade {

    private var fromBlob: String = ""
    private var toBlob: String = ""
    private var isVerbose = false

    private val testTasks by lazy {
        taskDependencies.getDependencies(this)
            .filterIsInstance<TestDiffTask>()
    }

    override fun setFromBlob(fromBlob: String) {
        this.fromBlob = fromBlob
        copyOptionsToDependencies()
    }

    override fun setToBlob(toBlob: String) {
        this.toBlob = toBlob
        copyOptionsToDependencies()
    }

    override fun setVerbose(isVerbose: Boolean) {
        this.isVerbose = isVerbose
        copyOptionsToDependencies()
    }

    private fun copyOptionsToDependencies() {
        testTasks.forEach(::copyOptionsTo)
    }

    private fun copyOptionsTo(facade: TestDiffTask) {
        facade.setFromBlob(fromBlob)
        facade.setToBlob(toBlob)
        facade.setVerbose(isVerbose)
    }

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