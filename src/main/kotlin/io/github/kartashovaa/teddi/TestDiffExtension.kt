package io.github.kartashovaa.teddi

import org.gradle.api.Project
import org.gradle.api.provider.ListProperty

interface TestDiffExtension {
    val ignoredVariants: ListProperty<String>

    companion object {
        fun get(project: Project) = project.extensions.getByType(TestDiffExtension::class.java)
        fun register(project: Project) = project.extensions.create("teddi", TestDiffExtension::class.java)
    }
}