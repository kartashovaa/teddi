package me.kyd3snik.test.diff.utils

import org.gradle.api.Project
import java.lang.module.ModuleDescriptor.Version
import com.android.builder.model.Version as AgpVersion

object AgpVersion {

    val MIN = Version.parse("7.4.0")
    val MAX = Version.parse("7.4.0")

    val current: Version?
        get() = runCatching { AgpVersion.ANDROID_GRADLE_PLUGIN_VERSION }
            .map(Version::parse)
            .getOrNull()

    fun isAgpProject(project: Project): Boolean {
        val version = current
        return when {
            version == null -> false
            version < MIN -> error("AGP version $version is not supported")
            version > MAX -> {
                //TODO: log unstable
                true
            }

            else -> true
        }
    }

}
