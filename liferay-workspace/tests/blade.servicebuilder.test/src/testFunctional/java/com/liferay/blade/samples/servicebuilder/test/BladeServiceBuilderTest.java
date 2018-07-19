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

package com.liferay.blade.samples.servicebuilder.test;

import aQute.remote.util.JMXBundleDeployer;

import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.blade.sample.test.functional.utils.BladeSampleFunctionalActionUtil;
import com.liferay.portal.kernel.exception.PortalException;

import java.io.File;

import java.net.URL;

import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * @author Liferay
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BladeServiceBuilderTest {

	@AfterClass
	public static void cleanUpDependencies() throws Exception {
		new JMXBundleDeployer().uninstall(_fooApiJarBSN);
		new JMXBundleDeployer().uninstall(_fooServiceJarBSN);
		new JMXBundleDeployer().uninstall(_fooWebJarBSN);
	}

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(System.getProperty("jarFile"));

		final File fooApiJar = new File(System.getProperty("fooApiJarFile"));
		final File fooServiceJar = new File(
			System.getProperty("fooServiceJarFile"));
		final File fooWebJar = new File(System.getProperty("fooWebJarFile"));

		new JMXBundleDeployer().deploy(_fooApiJarBSN, fooApiJar);
		new JMXBundleDeployer().deploy(_fooServiceJarBSN, fooServiceJar);
		new JMXBundleDeployer().deploy(_fooWebJarBSN, fooWebJar);

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}

	@Test
	public void testCreateFoo() throws InterruptedException, PortalException {
		_webDriver.get(_portletURL.toExternalForm());

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _addButton);

		Assert.assertTrue(
			"Field1 is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _field1Form));

		_field1Form.sendKeys("ServiceBuilderWebTest");

		_field5Form.clear();

		_field5Form.sendKeys("field5");

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _saveButton);

		Assert.assertTrue(
			"Service Builder Table is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _table));

		Assert.assertTrue(
			"ServiceBuilderWebTest is not present in table",
			_table.getText().contains("ServiceBuilderWebTest"));
	}

	@Test
	public void testDeleteFoo() throws InterruptedException, PortalException {
		_webDriver.get(_portletURL.toExternalForm());

		BladeSampleFunctionalActionUtil.implicitWait(_webDriver);

		String url = _webDriver.getCurrentUrl();

		BladeSampleFunctionalActionUtil.implicitWait(_webDriver);

		String windowHandler = _webDriver.getWindowHandle();

		List<WebElement> rows = _webDriver.findElements(
			By.xpath(
				"//table[contains(@data-searchcontainerid,'foosSearchContainer')]/tbody/tr"));

		int originalRows = rows.size();

		Assert.assertTrue(
			"Liferay Icon Menus is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _lfrIconMenu));

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _lfrIconMenu);

		Assert.assertTrue(
			"Action Menu Delete is not clickable",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _lfrMenuDelete));

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _lfrMenuDelete);

		Assert.assertTrue(
			"Alert is not present!",
			BladeSampleFunctionalActionUtil.isAlertPresent(_webDriver));

		_webDriver.switchTo().window(windowHandler);

		Thread.sleep(1000);

		_webDriver.navigate().to(url);

		Assert.assertTrue(
			"Service Builder Table is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _table));

		rows = _webDriver.findElements(By.xpath(_tableRow));

		int newRows = rows.size();

		int expectedFoos = originalRows - 1;

		Assert.assertTrue(
			"Expected " + expectedFoos + " foos, but saw " + newRows + " foos",
			newRows == expectedFoos);	}

	@Test
	public void testReadFoo() throws PortalException {
		_webDriver.get(_portletURL.toExternalForm());

		Assert.assertTrue(
			"Service Builder Table is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _table));

		Assert.assertTrue(
			"new field5 entry is not present in table",
			_table.getText().contains("new field5 entry"));
	}

	@Test
	public void testUpdateFoo() throws InterruptedException, PortalException {
		_webDriver.get(_portletURL.toExternalForm());

		BladeSampleFunctionalActionUtil.implicitWait(_webDriver);

		Assert.assertTrue(
			"Liferay Icon menu is not visible",
			BladeSampleFunctionalActionUtil.isClickable(_webDriver, _lfrIconMenu));

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _lfrIconMenu);

		Assert.assertTrue(
			"Liferay Menu Edit is not visible",
			BladeSampleFunctionalActionUtil.isClickable(_webDriver, _lfrMenuEdit));

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _lfrMenuEdit);

		Assert.assertTrue(
			"Field 1 form is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _field1Form));

		_field1Form.clear();

		_field1Form.sendKeys("field1 with Updated Name");

		Assert.assertTrue(
			"Save button is not visible",
			BladeSampleFunctionalActionUtil.isClickable(_webDriver, _saveButton));

		BladeSampleFunctionalActionUtil.mouseOverClick(_webDriver, _saveButton);

		Assert.assertTrue(
			"Service Builder Table is not visible",
			BladeSampleFunctionalActionUtil.isVisible(_webDriver, _table));

		Assert.assertTrue(
			"Service Builder Table does not contain Updated Name",
			_table.getText().contains("field1 with Updated Name"));
	}

	private static String _fooApiJarBSN = "com.liferay.blade.basic.api";
	private static String _fooServiceJarBSN = "com.liferay.blade.basic.service";
	private static String _fooWebJarBSN = "com.liferay.blade.basic.web";

	@FindBy(xpath = "//span[@class='lfr-btn-label' and contains(.,'Add')]")
	private WebElement _addButton;

	@FindBy(xpath = "//input[contains(@id,'field1')]")
	private WebElement _field1Form;

	@FindBy(xpath = "//input[contains(@id,'field5')]")
	private WebElement _field5Form;

	@FindBy(xpath = "//div[contains(@id,'_com_liferay_blade_samples_servicebuilder_web')]/table//..//tr/td[6]")
	private WebElement _firstRowField5;

	@FindBy(xpath = "//table//..//div[@class='btn-group lfr-icon-menu']/a")
	private WebElement _lfrIconMenu;

	@FindBy(xpath = "//ul[contains(@class,'dropdown-menu')]/li[2]/a[contains(.,'Delete')]")
	private WebElement _lfrMenuDelete;

	@FindBy(xpath = "//ul[contains(@class,'dropdown-menu')]/li[1]/a[contains(.,'Edit')]")
	private WebElement _lfrMenuEdit;

	@PortalURL("com_liferay_blade_samples_servicebuilder_web")
	private URL _portletURL;

	@FindBy(xpath = "//span[@class='lfr-btn-label' and contains(.,'Save')]")
	private WebElement _saveButton;

	@FindBy(xpath = "//div[contains(@id,'_com_liferay_blade_samples_servicebuilder_web')]/table//..//tr[2]/td[6]")
	private WebElement _secondRowField5;

	@FindBy(xpath = "//table[contains(@data-searchcontainerid,'foosSearchContainer')]")
	private WebElement _table;

	private String _tableRow =
		"//table[contains(@data-searchcontainerid,'foosSearchContainer')]/tbody/tr";

	@Drone
	private WebDriver _webDriver;

}
