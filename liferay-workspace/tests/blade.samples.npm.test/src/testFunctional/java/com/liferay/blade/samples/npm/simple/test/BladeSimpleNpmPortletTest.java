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

package com.liferay.blade.samples.npm.simple.test;

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
public class BladeSimpleNpmPortletTest {

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(
			System.getProperty("simpleNpmPortletJarFile"));

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}

	@Test
	public void testBladeSimpleNpm() throws InterruptedException {
		_webDriver.get(_portletURL.toExternalForm());

		Assert.assertTrue(
			"Portlet was not deployed",
			BladeSampleFunctionalActionUtil.isVisible(
				_webDriver, _bladeNpmSimplePortlet));

		BladeSampleFunctionalActionUtil.mouseOverClick(
			_webDriver, _bladeNpmSimplePortlet);

		Assert.assertTrue(
			"Expected: SIMPLE NPM PORTLET, but saw: " +
				_portletTitle.getText(),
			_portletTitle.getText().contentEquals("SIMPLE NPM PORTLET"));

		Assert.assertTrue(
			"Expected: Portlet main module loaded..., but saw: " +
				_portletBodyPre.getText(),
			_portletBodyPre.getText().contains("Portlet main module loaded."));
	}

	@FindBy(xpath = "//section[contains(@id,'SimpleNpmPortlet')]")
	private WebElement _bladeNpmSimplePortlet;

	@FindBy(xpath = "//section[contains(@id,'SimpleNpmPortlet')]//..//div/pre")
	private WebElement _portletBodyPre;

	@FindBy(xpath = "//section[contains(@id,'SimpleNpmPortlet')]//..//div/h2")
	private WebElement _portletTitle;

	@PortalURL("com_liferay_blade_npm_simple_npm_portlet_SimpleNpmPortlet")
	private URL _portletURL;

	@Drone
	private WebDriver _webDriver;

}