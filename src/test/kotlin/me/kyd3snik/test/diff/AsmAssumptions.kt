package me.kyd3snik.test.diff

import org.junit.Test
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.io.FileInputStream
import java.io.InputStream

object Mapper {
    fun map(): Unit = TODO()
}

class AsmAssumptions {

    @Test
    fun run() {
        val reader = ClassReader("me.kyd3snik.test.diff.AsmAssumptions")
        reader.accept(TestClassVisitor(), 0)
    }

    @Test
    fun analyse() {
        val analyser = ClassAnalyser()
        val classes =
            analyser.getUsedClasses(FileInputStream("build/classes/kotlin/test/me/kyd3snik/test/diff/AsmAssumptions.class"))
        println(classes)

    }

    fun test() {
        val a = Mapper

        a.map()
    }
}

class ClassAnalyser {

    fun getUsedClasses(inputStream: InputStream): Set<String> {
        val reader = ClassReader(inputStream)
        val collector = ClassUsageCollector()
        reader.accept(MethodClassVisitor(collector), 0)
        return collector.visitedClasses
    }
}

class MethodClassVisitor(private val methodVisitor: MethodVisitor) : ClassVisitor(Opcodes.ASM9) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor = methodVisitor
}

class ClassUsageCollector : MethodVisitor(Opcodes.ASM9) {
    private val _visitedClasses = HashSet<String>()

    val visitedClasses: Set<String> get() = _visitedClasses.map { it.replace("/", ".") }.toSet()

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        owner?.let(_visitedClasses::add)
    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        owner?.let(_visitedClasses::add)
    }
}

class TestClassVisitor : ClassVisitor(Opcodes.ASM9) {
    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor = TestMethodVisitor()
}

class TestMethodVisitor : MethodVisitor(Opcodes.ASM9) {
    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        println("\tinvoke $owner#$name")
    }

    override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) {
        println("\taccess $owner#$name")
    }
}