[![Github Actions Demo](https://github.com/kartashov-a/teddi/actions/workflows/github-actions-demo.yml/badge.svg)](https://github.com/kartashov-a/teddi/actions/workflows/github-actions-demo.yml)

## Teddi

Gradle plugin for testing diffs between commits

Runs a test if:

1) The test file itself is changed
2) The file containing class under test is changed

### Usage

```groovy
// settings.gradle
buildscript {
    repositories {
        mavenLocal()
    }

    dependencies {
        classpath "me.kyd3snik:teddi:0.0.1"
    }
}
```

```groovy
// app/build.gradle
plugins {
    id 'me.kyd3snik.teddi'
}
```

### Get started

Publishing project to maven local and testing last commit for "Debug" variant

```console
$ git clone https://github.com/kartashov-a/teddi.git
$ cd teddi
$ ./gradlew publishToMavenLocal
$ cd <target-project-dir>
$ ./gradlew app:testDiffDebugUnitTest -PfromBlob=HEAD~1
```