- [ ] Support different AGP versions

```kotlin
// TODO: to use this extension set experimental option
project.extensions.findByType(ApplicationExtension::class.java)?.let { ext ->
    ext.sourceSets.first()
}

// TODO: new extensions, crashes on current project
//  use different extension depending on AGP version
project.extensions.findByType(ApplicationAndroidComponentsExtension::class.java)?.let { ext ->
    ext.onVariants {
        it.sources.java?.all?.get()?.all { }
        it.unitTest?.sources?.java?.all?.get()?.all { }
    }
}
```

- [ ] Add tests
    - [ ] Closest for SomeViewModelTest: SomeViewModel(correct), SomeViewModelFactory
        - Change viewModel -> triggers test
        - Change factory -> ignores test
    - [ ] Same package in different modules
        - change file in module B doesn't trigger tests in module A
- [ ] Setup workflows
  - [ ] Publish Java Package with Gradle
  - [ ] Detekt
