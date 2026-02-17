# kotest-fundamentals

Essentials
- Add dependency: testImplementation("io.kotest:kotest-runner-junit5:<version>") and kotest-assertions, kotest-property as needed.
- Use test styles: StringSpec, FunSpec, BehaviorSpec, DescribeSpec depending on preference.

Matchers
- shouldBe, shouldNotBe, shouldContain, shouldHaveSize, shouldStartWith, etc.

Coroutine testing
- Use kotlin.test's runTest or Kotest's built-in coroutine support; prefer TestCoroutineDispatcher/TestScope patterns when mocking time.

Property testing
- Use kotest-property for generators and forAll blocks to validate invariants across many cases.

Example (FunSpec)
```
class SampleTest : FunSpec({
  test("sum works") {
    (1 + 2) shouldBe 3
  }
})
```

(End of reference)
