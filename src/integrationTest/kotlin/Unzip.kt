import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

fun unzipResource(name: String, outputDir: File) {
    require(outputDir.isDirectory && outputDir.exists() || outputDir.mkdirs())

    val classLoader = Thread.currentThread().contextClassLoader
    val resourceStream = requireNotNull(classLoader.getResourceAsStream(name)) { "Resource not found" }
    ZipInputStream(resourceStream).use { zip ->
        var entry = zip.nextEntry
        while (entry != null) {
            if (!entry.isDirectory) {
                val destination = File(outputDir, entry.name)
                check(!destination.exists()) { "${destination.path} already exists!" }

                val parent = destination.parentFile
                check(parent.exists() || parent.mkdirs()) { "Can't create ${destination.parent}" }
                check(destination.createNewFile()) { "Can't create file ${destination.path}" }

                zip.copyTo(FileOutputStream(destination))
            }

            entry = zip.nextEntry
        }
    }
}