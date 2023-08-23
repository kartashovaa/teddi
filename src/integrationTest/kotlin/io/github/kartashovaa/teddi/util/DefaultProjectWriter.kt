package io.github.kartashovaa.teddi.util

import java.io.File

class DefaultProjectWriter(override val projectDir: File) : TeddiProjectWriter,
    VCSProjectWriter by DefaultVCSProjectWriter(projectDir) {

    override fun write(path: String, content: String) =
        File(projectDir, path).apply {
            assert(parentFile.exists() || parentFile.mkdirs())
            assert(createNewFile())
            writeText(content)
        }


    override fun subproject(name: String): DefaultProjectWriter = DefaultProjectWriter(File(projectDir, name))
}
