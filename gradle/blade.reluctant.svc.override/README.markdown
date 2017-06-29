# Reluctant Service Override

This module's OSGi Configuration Admin file and
`com.liferay.blade.reluctant.svc.api.SomeService` implementation
`CustomServiceImpl` binds to `CustomServiceImpl` to
`com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet`'s
`SomeService` field that uses a reluctant policy options. This module depends on the following modules to override that field:

-   `com.liferay.blade.reluctant.svc.api`
-   `blade.reluctant.svc.reference`

Here's `CustomServiceImpl`'s `@Component` annotation:

    @Component(
       immediate = true, property = {"service.ranking:Integer=100"},
       service = SomeService.class
       )

The attribute `immediate = true` informs Liferay's OSGi runtime to activate the
component immediately upon resolution. The property
`"service.ranking:Integer=100"` prioritizes the component above all other
`SomeService` service components that have a ranking less than `100`. Lastly,
the component provides an OSGi service of type `SomeService.class`.

Here's the rest of the `CustomServiceImpl` class:

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

Note, rather than inheriting from `SomeServiceImpl`, `CustomServiceImpl`
implements `SomeService` and instead refers to a `SomeServiceImpl` object (via
an` @Reference` annotated field) to delegate to it.

Configuration Admin files
`configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.config`
(or
`configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.cfg`
for CE Portal 7.0 GA3 or earlier) can be deployed to the Liferay instance's
`osgi/configs/` folder to configure `ReluctantServiceReferencePortlet`'s
`_someService` field to bind a `CustomServiceImpl` object to it. Here's the configuration content:

    _someService.target=(component.name=com.liferay.blade.reluctant.svc.override.CustomServiceImpl)

The file's name designates the configuration for the `com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet` class. The class's `_someService` field is targeted to use this module's `SomeService` component `com.liferay.blade.reluctant.svc.override.CustomServiceImpl`.

Here are the steps to override `ReluctantServiceReferencePortlet`'s reluctant `SomeService` service reference:

1.  Deploy sample module `com.liferay.blade.reluctant.svc.api` to define the OSGi service `SomeService`. 

        cd com.liferay.blade.reluctant.svc.api
        blade deploy

2.  Deploy sample module `blade.reluctant.svc.reference`, whose `SomeService` reference you're overriding. 
3.  Add `blade.reluctant.svc.reference` module's portlet *Reluctant Service Reference Portlet* to a portal page. 
4.  Deploy this sample module `blade.reluctant.svc.override`. 
5.  Add configuration file  `configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.config`
(or
`configs/com.liferay.blade.reluctant.svc.reference.portlet.ReluctantServiceReferencePortlet.cfg`
for CE Portal 7.0 GA3 or earlier) to your `Liferay-Home/osgi/configs` folder. 
6.  Refresh the portal page that has *Reluctant Service Reference Portlet*.

![Deploying the Configuration Admin file binds the portlet's `SomeService` reference to `CustomServiceImpl`.](images/providing-a-some-service-impl-that-delegates.png)

Portlet *Reluctant Service Reference Portlet* shows that it now uses the `com.liferay.blade.reluctant.svc.override.CustomServiceImpl` service component instead of the portlet's default component `com.liferay.blade.reluctant.svc.reference.impl.SomeServiceImpl `.  
