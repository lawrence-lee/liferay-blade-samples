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

package com.liferay.blade.samples.npm.vuejs.test;

import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.blade.sample.test.functional.utils.BladeSampleFunctionalActionUtil;

import java.io.File;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Lawrence Lee
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BladeVueJsNpmPortletTest {

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(
			System.getProperty("vuejsNpmPortletJarFile"));

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}

	@Ignore //does not work in headless mode
	@Test
	public void testBladeVuejsNpm() throws InterruptedException {
		_webDriver.get(_portletURL.toExternalForm());

		BladeSampleFunctionalActionUtil.implicitWait(_webDriver);

		Assert.assertTrue(
			"Portlet was not deployed",
			BladeSampleFunctionalActionUtil.isVisible(
				_webDriver, _bladeNpmVuejsPortlet));

		Assert.assertTrue(
			"Expected: VUE.JS PORTLET, but saw: " + _portletTitle.getText(),
			_portletTitle.getText().contentEquals("VUE.JS PORTLET"));

		Assert.assertTrue(
			"Expected:Whatever else humans are supposed to eat, but saw: " +
				_portletListItem.getText(),
			_portletListItem.getText().contentEquals(
				"Whatever else humans are supposed to eat"));

		Assert.assertTrue(
			"Expected: Hello from Vue.js!, but saw: " +
				_portletReversibleMessage.getText(),
			_portletReversibleMessage.getText().contentEquals(
				"Hello from Vue.js!"));

		BladeSampleFunctionalActionUtil.mouseOverClick(
			_webDriver, _portletBodyButton);

		Assert.assertTrue(
			"Expected: !sj.euV morf olleH, but saw: " +
				_portletReversibleMessage.getText(),
			BladeSampleFunctionalActionUtil.isTextPresent(
				_webDriver,
				_portletReversibleMessage,
				"!sj.euV morf olleH"));
	}

	@FindBy(xpath = "//section[contains(@id,'VuejsPortlet')]")
	private WebElement _bladeNpmVuejsPortlet;

	@FindBy(xpath = "//section[contains(@id,'VuejsPortlet')]//..//div[@class='portlet-body']/div/button")
	private WebElement _portletBodyButton;

	@FindBy(xpath = "//section[contains(@id,'VuejsPortlet')]//..//ol/li[3]")
	private WebElement _portletListItem;

	@FindBy(xpath = "//section[contains(@id,'VuejsPortlet')]//..//div[@class='portlet-body']/div/p[2]")
	private WebElement _portletReversibleMessage;

	@FindBy(xpath = "//section[contains(@id,'VuejsPortlet')]//..//div/h2")
	private WebElement _portletTitle;

	@PortalURL("com_liferay_blade_npm_vuejs_portlet_VuejsPortlet")
	private URL _portletURL;

	@Drone
	private WebDriver _webDriver;

}