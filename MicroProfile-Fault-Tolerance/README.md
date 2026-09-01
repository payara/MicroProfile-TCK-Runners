# MicroProfile Fault Tolerance TCK runner

This repository contains:
 
 - tck-runner - a template project to run the MicroProfile TCK suite against a custom implementation

# Some notes and requirements of the TCK

The TCK tests the MicroProfile Fault Tolerance API, which depends on CDI 1.1 API.

The TCK tests are designed as Arquillian tests, which run tests in an isolated container against a test application package. The test application is always packaged as WAR application, therefore the arquillian adapter used must support WAR deployment.

**NOTES:**

* The deployment-validation tests (the `illegalConfig` and `invalidParameters`
  packages, and the four negative `fallbackmethod` tests) expect the deployment
  to be rejected with a `FaultToleranceDefinitionException`. Payara Micro deploys
  applications with `--loadOnly`, so it logs the validation failure but boots
  anyway and the managed Arquillian connector reports a successful deployment. An
  Arquillian observer, `DeploymentFailureDetector` (see `src/test/java`),
  captures Micro's console output around each deployment and, when it sees the
  failure, rethrows it as a deployment exception whose type matches the test's
  `@ShouldThrowException`. It only uses Arquillian SPI, so it is a no-op on the
  full-server and embedded profiles (where failed deployments already throw
  directly).
* On the `payara-micro-managed` profile every test starts its own Payara Micro
  JVM, which must print its startup banner within the connector's startup
  timeout (~180s). Avoid running other heavy builds concurrently with the suite:
  CPU starvation can push a Micro instance past that timeout, which the connector
  reports as `No applications were found deployed to Payara Micro` — a false
  failure unrelated to the test.
* If the timeout tests prove flaky on slow/loaded CI, uncomment the
  `org.eclipse.microprofile.fault.tolerance.tck.timeout.multiplier` system
  property in `pom.xml` to relax their timing.