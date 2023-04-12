## Teddi

Gradle plugin for testing diffs between commits

### Usage

```groovy
// settings.gradle
buildscript {
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

### Run

```console
$ ./gradlew app:testDiffUnitTest -PfromBlob=HEAD~1 -PtoBlob=HEAD
```