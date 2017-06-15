# Reluctant Service API

This module provides an interface called `SomeService`. The interface has a
single method `doSomething`.

Sample module `blade.reluctant.svc.reference` consumes the service and has a
class that implements `SomeService`. Sample module
`blade.reluctant.svc.override` provides a `SomeService` implementation too. Both
modules depend on this module being active to define `SomeService`.