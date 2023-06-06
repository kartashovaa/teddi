import java.io.File

class TestProjectInflater(private val rootDir: File) {

    fun inflate(project: TestProject) {
        if (rootDir.exists()) {
            rootDir.listFiles()?.forEach { file -> file.deleteRecursively() }
        }

        project.files.forEach(::fillFile)
    }

    private fun fillFile(fileName: String, content: String) {
        val file = File(rootDir, fileName)
        check(file.parentFile.let { it.exists() || it.mkdirs() })
        check(file.createNewFile())
        file.writeText(content)

    }
}