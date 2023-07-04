package me.kyd3snik.test.diff.utils

import org.gradle.api.Task
import org.gradle.util.GradleVersion

object TaskCompat {

    fun notCompatibleWithConfigurationCache(task: Task, reason: String) {
        if (GradleVersion.current() >= GradleVersion.version("7.4")) {
            @Suppress("UnstableApiUsage")
            task.notCompatibleWithConfigurationCache(reason)
        }
    }
}