package me.kyd3snik.test.diff.changes

import org.gradle.api.Transformer
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.provider.DefaultProperty
import org.gradle.api.internal.provider.PropertyHost
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.process.internal.ExecActionFactory
import org.junit.Test
import java.io.File
import java.util.function.BiFunction

class TestRegularFileProperty : RegularFileProperty {
    override fun get(): RegularFile = TODO("Not yet implemented")
    override fun getOrNull(): RegularFile? = TODO("Not yet implemented")
    override fun getOrElse(defaultValue: RegularFile): RegularFile = TODO("Not yet implemented")
    override fun <S : Any?> map(transformer: Transformer<out S, in RegularFile>): Provider<S> =
        TODO("Not yet implemented")

    override fun <S : Any?> flatMap(transformer: Transformer<out Provider<out S>, in RegularFile>): Provider<S> =
        TODO("Not yet implemented")

    override fun isPresent(): Boolean = TODO("Not yet implemented")
    override fun orElse(value: RegularFile): Provider<RegularFile> = TODO("Not yet implemented")
    override fun orElse(provider: Provider<out RegularFile>): Provider<RegularFile> = TODO("Not yet implemented")

    @Deprecated("Deprecated in Java", ReplaceWith("TODO(\"Not yet implemented\")"))
    override fun forUseAtConfigurationTime(): Provider<RegularFile> = TODO("Not yet implemented")
    override fun <B : Any?, R : Any?> zip(right: Provider<B>, combiner: BiFunction<RegularFile, B, R>): Provider<R> =
        TODO("Not yet implemented")

    override fun finalizeValue() = Unit
    override fun finalizeValueOnRead() = Unit
    override fun disallowChanges() = Unit
    override fun disallowUnsafeRead() = Unit
    override fun set(file: File?) = Unit
    override fun set(value: RegularFile?) = Unit
    override fun set(provider: Provider<out RegularFile>) = Unit
    override fun value(value: RegularFile?): RegularFileProperty = TODO("Not yet implemented")
    override fun value(provider: Provider<out RegularFile>): RegularFileProperty = TODO("Not yet implemented")
    override fun convention(value: RegularFile?): RegularFileProperty = TODO("Not yet implemented")
    override fun convention(provider: Provider<out RegularFile>): RegularFileProperty = TODO("Not yet implemented")
    override fun getAsFile(): Provider<File> = TODO("Not yet implemented")
    override fun fileValue(file: File?): RegularFileProperty = TODO("Not yet implemented")
    override fun fileProvider(provider: Provider<File>): RegularFileProperty = TODO("Not yet implemented")
    override fun getLocationOnly(): Provider<RegularFile> = TODO("Not yet implemented")
}

class CollectChangesTaskTest {

    private val task = object : CollectChangesTask() {
        override val fromBlob: Property<String?> = DefaultProperty(PropertyHost.NO_OP, String::class.java)
        override val toBlob: Property<String?> = DefaultProperty(PropertyHost.NO_OP, String::class.java)
        override val output: RegularFileProperty = TestRegularFileProperty()
        override fun getExecActionFactory(): ExecActionFactory {
            return super.getExecActionFactory()
        }
    }

    @Test
    fun register() {
//        TODO(
//            """
//                verify project has task "collectChanges"
//                verify runs "git diff" with properties fromBlob, toBlob from project
//            """.trimIndent()
//        )
    }
}