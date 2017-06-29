# Reluctant Service Override

This module 
With the help of an OSGi Configuration Admin file, this module's
`com.liferay.blade.reluctant.svc.api.SomeService` implementation
`CustomServiceImpl` can be bound to `SomeService` service references that have
reluctant policy options.

This module depends on the API module `com.liferay.blade.reluctant.svc.api`. The
`blade.reluctant.svc.reference` sample module refers to the `SomeService`
service interface.

Here's `CustomServiceImpl`'s `@Component` annotation:

    @Component(
       immediate = true, property = {"service.ranking:Integer=100"},
       service = SomeService.class
       )

The attribute `immediate = true` informs Liferay's OSGi runtime to activate the
component immediately upon resolution. The property
`"service.ranking:Integer=100"` prioritizes the component above all other
`SomeService` service components that have a ranking less than `100`. Lastly,
the component provides service of type `SomeService.class`.

Here's the rest of the class:

    public class CustomServiceImpl implements SomeService {

    	@Override
    	public String doSomething() {
    		StringBuilder sb = new StringBuilder();

    		Class<?> clazz = getClass();

    		sb.append(clazz.getName());

    		sb.append(", which delegates to ");
    		sb.append(_defaultService.doSomething());

    		return sb.toString();
    	}

    	@Reference (
    		target = "(component.name=com.liferay.blade.reluctant.svc.reference.impl.SomeServiceImpl)",
    		unbind = "-"
    	)
    	private SomeService _defaultService;

    }

Note, rather than inheriting from `SomeServiceImpl`, `CustomServiceImpl` refers
to a `SomeServiceImpl` object (via an` @Reference` annotated field) and
delegates to it.

Configuration Admin files
`configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.config`
(or
`configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.cfg`
for CE Portal 7.0 GA3 or earlier) can be deployed to the Liferay instance's
`osgi/configs/` folder to configure `ReluctantServiceReferencePortlet`'s
`_someService` field to bind to this module's `SomeService` implementation
`CustomServiceImpl`.

![Deploying the Configuration Admin file binds the portlet's `SomeService` reference to `CustomServiceImpl`.](images/providing-a-some-service-impl-that-delegates.png)

Deploying this module and copying the Configuration Admin file to the
`osgi/configs` folder binds `ReluctantServiceReferencePortlet`'s `SomeService`
reference to `CustomServiceImpl`.
