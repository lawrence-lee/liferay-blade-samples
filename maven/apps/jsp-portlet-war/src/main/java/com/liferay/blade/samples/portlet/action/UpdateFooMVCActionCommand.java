/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.blade.samples.portlet.action;


import com.liferay.blade.samples.servicebuilder.model.Foo;
import com.liferay.blade.samples.servicebuilder.service.FooLocalService;
import com.liferay.blade.samples.servicebuilder.service.FooLocalServiceUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Liferay
 */
@Component(
        immediate = true,
        property = {
                "javax.portlet.name=bladeliferaymvc",
                "mvc.command.name=/updateFoo"
        },
        service = MVCActionCommand.class
)
public class UpdateFooMVCActionCommand extends BaseMVCActionCommand {
    @Override
    protected void doProcessAction(ActionRequest actionRequest, ActionResponse actionResponse) throws Exception {
         long fooId = ParamUtil.getLong(actionRequest, "fooId");

        String field1 = ParamUtil.getString(actionRequest, "field1");
        boolean field2 = ParamUtil.getBoolean(actionRequest, "field2");
        int field3 = ParamUtil.getInteger(actionRequest, "field3");
        String field5 = ParamUtil.getString(actionRequest, "field5");

        int dateMonth = ParamUtil.getInteger(actionRequest, "field4Month");
        int dateDay = ParamUtil.getInteger(actionRequest, "field4Day");
        int dateYear = ParamUtil.getInteger(actionRequest, "field4Year");
        int dateHour = ParamUtil.getInteger(actionRequest, "field4Hour");
        int dateMinute = ParamUtil.getInteger(actionRequest, "field4Minute");
        int dateAmPm = ParamUtil.getInteger(actionRequest, "field4AmPm");

        if (dateAmPm == Calendar.PM) {
            dateHour += 12;
        }

        Date field4 = PortalUtil.getDate(
                dateMonth, dateDay, dateYear, dateHour, dateMinute,
                PortalException.class);

        if (fooId <= 0) {
            _log.info("Adding a new foo...");

            Foo foo = getFooLocalService().createFoo(0);

            foo.setField1(field1);
            foo.setField2(field2);
            foo.setField3(field3);
            foo.setField4(field4);
            foo.setField5(field5);
            foo.isNew();
            getFooLocalService().addFooWithoutId(foo);
        }
        else {
            _log.info("Updating a new foo...");

            Foo foo = getFooLocalService().fetchFoo(fooId);

            foo.setFooId(fooId);
            foo.setField1(field1);
            foo.setField2(field2);
            foo.setField3(field3);
            foo.setField4(field4);
            foo.setField5(field5);
            getFooLocalService().updateFoo(foo);
        }

    }

    public FooLocalService getFooLocalService() {
        return _fooLocalService;
    }

    @Reference
    private volatile FooLocalService _fooLocalService;

    private static final Log _log =
            LogFactoryUtil.getLog(UpdateFooMVCActionCommand.class);
}
