/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.samples.configurationaction;

import com.liferay.blade.samples.configurationaction.constants.BladeMessagePortletKeys;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liferay
 */
@Component(
	configurationPid = "com.liferay.blade.samples.configurationaction.MessageDisplayConfiguration",
	configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true,
	property = {
		"javax.portlet.name=" + BladeMessagePortletKeys.BladeMessagePortlet
	},
	service = ConfigurationAction.class
)
public class MessageDisplayConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		if (_logger.isDebugEnabled()) {
			_logger.debug("Blade Message Portlet configuration include");
		}

		httpServletRequest.setAttribute(
			MessageDisplayConfiguration.class.getName(),
			_messageDisplayConfiguration);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		if (_logger.isDebugEnabled()) {
			_logger.debug("Blade Message Portlet configuration action");
		}

		String fontColor = ParamUtil.getString(actionRequest, "fontColor");
		String fontFamily = ParamUtil.getString(actionRequest, "fontFamily");
		String fontSize = ParamUtil.getString(actionRequest, "fontSize");

		if (_logger.isDebugEnabled()) {
			_logger.debug(
				"Message Display Configuration: Font Family:" + fontFamily);
			_logger.debug(
				"Message Display Configuration: Font Size:" + fontSize);
			_logger.debug(
				"Message Display Configuration: Font Color:" + fontColor);
		}

		setPreference(actionRequest, "fontColor", fontColor);
		setPreference(actionRequest, "fontFamily", fontFamily);
		setPreference(actionRequest, "fontSize", fontSize);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) {
		_messageDisplayConfiguration = ConfigurableUtil.createConfigurable(
			MessageDisplayConfiguration.class, properties);
	}

	private Logger _logger = LoggerFactory.getLogger(getClass().getName());
	private volatile MessageDisplayConfiguration _messageDisplayConfiguration;

}