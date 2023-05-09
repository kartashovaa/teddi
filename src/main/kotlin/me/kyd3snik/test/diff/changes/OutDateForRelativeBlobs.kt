package me.kyd3snik.test.diff.changes

import org.gradle.api.Task
import org.gradle.api.specs.Spec

class OutDateForRelativeBlobs : Spec<Task> {

    override fun isSatisfiedBy(element: Task): Boolean {
        if (element !is CollectChangesTask) return true
        //TODO:
        // 1) research what kinds of relative blobs git provides
        // 2) add warning and option to suppress it(via properties or extension). provide the description of disabling warning in warning
        // 3) suggest running task like "gradlew app:testDiffUnitTest -PfromBlob=$(git rev-parse HEAD~1)", which solves problem with relative path
        return element.fromBlob.orNull?.contains("HEAD") != true &&
                element.toBlob.orNull?.contains("HEAD") != true
    }
}