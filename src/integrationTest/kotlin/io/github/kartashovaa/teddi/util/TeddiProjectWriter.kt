package io.github.kartashovaa.teddi.util

interface TeddiProjectWriter : AndroidProjectWriter, GradleProjectWriter, VCSProjectWriter {

    override fun subproject(name: String): TeddiProjectWriter
}