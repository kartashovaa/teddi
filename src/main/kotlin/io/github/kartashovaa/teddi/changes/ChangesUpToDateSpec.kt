package io.github.kartashovaa.teddi.changes

import org.gradle.api.Task
import org.gradle.api.specs.Spec

class ChangesUpToDateSpec {

    //TODO:
    // 0) think about parameters which break build cache(non committed changes, HEAD, relative commits etc)
    // 1) research what kinds of relative blobs git provides
    // 2) add warning and option to suppress it(via properties or extension). provide the description of disabling warning in warning
    // 3) suggest running task like "gradlew app:testDiffUnitTest -PfromBlob=$(git rev-parse HEAD~1)", which solves problem with relative path
    fun isSatisfiedBy(fromBlob: String?, toBlob: String?): Boolean =
        fromBlob != null && !fromBlob.contains("HEAD") && toBlob?.contains("HEAD") == false

    fun asTaskSpec(): Spec<Task> = Spec<Task> { element ->
        element is CollectChangesTask && isSatisfiedBy(element.fromBlob.orNull, element.toBlob.orNull)
    }
}