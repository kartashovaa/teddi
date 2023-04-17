## Teddi

Gradle plugin for testing diffs between commits

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

Publishing project to maven local and testing last commit

```console
$ git clone https://github.com/kartashov-a/teddi.git
$ cd teddi
$ ./gradlew publishToMavenLocal
$ cd <target-project-dir>
$ ./gradlew app:testDiffUnitTest -PfromBlob=HEAD~1
```