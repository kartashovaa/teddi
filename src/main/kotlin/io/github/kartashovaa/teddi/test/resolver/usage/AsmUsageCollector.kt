package io.github.kartashovaa.teddi.test.resolver.usage

import groovyjarjarasm.asm.Opcodes
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import java.io.File

class AsmUsageCollector : UsageCollector {

    override fun collect(classFile: File): Set<String> {
        assert(classFile.exists())
        val classReader = ClassReader(classFile.inputStream())
        val visitor = UsageClassVisitor()
        classReader.accept(visitor, 0)
        return visitor.usages
    }

    private class UsageClassVisitor : ClassVisitor(Opcodes.ASM9) {

        private val methodVisitor = UsageMethodVisitor()

        val usages: Set<String> get() = methodVisitor.usages

        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor = methodVisitor
    }

    private class UsageMethodVisitor : MethodVisitor(Opcodes.ASM9) {

        private val _usages = HashSet<String>()

        val usages: Set<String> get() = HashSet(_usages)

        override fun visitMethodInsn(
            opcode: Int,
            owner: String?,
            name: String?,
            descriptor: String?,
            isInterface: Boolean
        ) = recordUsage(owner)

        override fun visitFieldInsn(opcode: Int, owner: String?, name: String?, descriptor: String?) =
            recordUsage(owner)

        private fun recordUsage(owner: String?) {
            if (owner != null) _usages.add(owner)
        }
    }
}