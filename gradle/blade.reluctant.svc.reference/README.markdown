# Reluctant Service Reference

This module's `GenericPortlet` `ReluctantServiceReferencePortlet` consumes
an OSGi service `com.liferay.blade.reluctant.svc.api.SomeService` and prints a message in the portlet indicating the
bound `SomeService` implementation class it calls.

![The portlet calls on `SomeService` implementation `SomeServiceImpl`, by default.](images/using-default-service-impl.png)

This module depends on module `blade.reluctant.svc.api` for its
`SomeService` interface.

`ReluctantServiceReferencePortlet`'s field `_someService` uses an
`@Reference` annotation, whose policy is static and reluctant by default.

	@Reference (unbind = "-")
	private SomeService _someService;

The annotation's attribute setting `unbind = "-"` indicates that the registrator
class doesn't use any method for unbinding the service. If no higher ranked `SomeService` implementation is available at deployment, the portlet is bound to
this module's default implementation `SomeServiceImpl`.

This module can be used with module `blade.reluctant.svc.override`, which
provides a `SomeService` implementation `CustomServiceImpl`.  That module's Configuration
Admin files
`confs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.config` (or `configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.cfg` for CE Portal 7.0 GA3 or earlier)
can be deployed to the Liferay instance's `osgi/configs/` folder to configure
`ReluctantServiceReferencePortlet`'s `_someService` field to bind to
`CustomServiceImpl`.

![Deploying the Configuration Admin file binds the portlet's `SomeService` reference to `CustomServiceImpl`.](images/providing-a-some-service-impl-that-delegates.png)

Deploying the `blade.reluctant.svc.override` module and copying its Configuration Admin file to the
`osgi/configs` folder binds `ReluctantServiceReferencePortlet`'s `SomeService`
reference to `CustomServiceImpl`.