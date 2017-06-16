# Reluctant Service Reference

This module's `GenericPortlet` `ReluctantServiceReferencePortlet` consumes an
OSGi service `com.liferay.blade.reluctant.svc.api.SomeService` and prints a
message in the portlet indicating the bound `SomeService` implementation class
it calls. Here's the portlet's `doView` method:

	@Override
	protected void doView(RenderRequest request, RenderResponse response)
		throws IOException, PortletException {

		PrintWriter printWriter = response.getWriter();

		printWriter.println("I'm calling service ...<br>");

		printWriter.println(_someService.doSomething());
	}

The method prints the `String` returned from calling the service's `doSomething` method.

This module depends on module `blade.reluctant.svc.api` for its `SomeService`
interface.

`ReluctantServiceReferencePortlet`'s field `_someService` uses an `@Reference`
annotation, whose policy is static and reluctant by default.

	@Reference (unbind = "-")
	private SomeService _someService;

The annotation's attribute setting `unbind = "-"` indicates that the registrator
class doesn't use any method for unbinding the service. If no higher ranked
`SomeService` implementation is available at deployment, the portlet is bound to
this module's default implementation `SomeServiceImpl`, which has a default service ranking value `0`.

Steps to deploy the portlet:

1.  Deploy sample module `com.liferay.blade.reluctant.svc.api` to define the
    OSGi service `SomeService`. 

        cd com.liferay.blade.reluctant.svc.api
        blade deploy

2.  Deploy this sample module `blade.reluctant.svc.reference`.
3.  Add this module's portlet *Reluctant Service Reference Portlet* to a portal
    page. 

The portlet calls on this module's `SomeService` implementation `SomeServiceImpl`

![The portlet calls on `SomeService` implementation `SomeServiceImpl`, by default.](images/using-default-service-impl.png)

For details on overriding `ReluctantServiceReferencePortlet`'s static reluctant
reference to `SomeService`, see sample module `blade.reluctant.svc.override`'s
README file. 
