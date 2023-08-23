package io.github.kartashovaa.teddi.util

import org.eclipse.jgit.api.Git
import java.io.File

class DefaultVCSProjectWriter(private val projectDir: File) : VCSProjectWriter {

    private val repository: Git by lazy {
        check(!File(projectDir, ".git").exists()) { "Git repository already initialized" }
        Git.init()
            .setDirectory(projectDir)
            .call()
    }

    override fun commit(message: String): String {
        repository.add()
            .addFilepattern(".")
            .call()

        return repository.commit()
            .setMessage(message)
            .call()
            .name
    }
}