package io.github.kartashovaa.teddi

import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty

abstract class TestDiffExtension {
    abstract val ignoredBuildTypes: ListProperty<String>
    abstract val ignoredVariants: ListProperty<String>

    @Suppress("DEPRECATION")
    fun isAcceptable(variant: BaseVariant): Boolean =
        variant.name !in ignoredVariants.get() &&
                variant.buildType.name !in ignoredBuildTypes.get()

    companion object {
        fun get(project: Project) = project.extensions.getByType(TestDiffExtension::class.java)
        fun register(project: Project) = project.extensions.create("teddi", TestDiffExtension::class.java)
    }
}