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

package com.liferay.blade.samples.update.portlet.test;

import aQute.lib.io.IO;

import aQute.remote.util.JMXBundleDeployer;

import com.liferay.arquillian.portal.annotation.PortalURL;
import com.liferay.blade.samples.integration.test.utils.BladeCLIUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
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

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Lawrence Lee
 */
@RunAsClient
@RunWith(Arquillian.class)
public class BladeSamplesUpdatePortletTest {

	@Deployment
	public static JavaArchive create() throws Exception {
		final File jarFile = new File(System.getProperty("jspPortletJarFile"));

		_modulesDir = new File(System.getProperty("modulesDir"));

		_projectPath = BladeCLIUtil.createProject(
			_modulesDir, "mvc-portlet", "helloworld");

		_buildStatus = BladeCLIUtil.execute(_projectPath, "gw", "assemble");

		File buildOutput = new File(
			_projectPath + "/build/libs/helloworld-1.0.0.jar");

		new JMXBundleDeployer().deploy(_helloWorldJarBSN, buildOutput);

		return ShrinkWrap.createFromZipFile(JavaArchive.class, jarFile);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		new JMXBundleDeployer().uninstall(_helloWorldJarBSN);

		if (_projectPath.exists()) {
			IO.delete(_projectPath);
			Assert.assertFalse(_projectPath.exists());
		}
	}

	@Test
	public void testUpdateMVCPortletProject() throws Exception {
		_webDriver.get(_portletURL.toExternalForm());

		Assert.assertTrue(
			"Portlet was not deployed", isVisible(_helloWorldPortlet));
		Assert.assertTrue(
			_portletTitle.getText(),
			_portletTitle.getText().contentEquals("helloworld Portlet"));
		Assert.assertTrue(
			_portletBody.getText(),
			_portletBody.getText().contentEquals("Hello from helloworld JSP!"));

		File dynamicFile = new File(
			_projectPath +
				"/src/main/java/helloworld/portlet/HelloworldPortlet.java");

		List<String> lines = new ArrayList<>();
		String line = null;

		try (BufferedReader reader =
				new BufferedReader(new FileReader(dynamicFile))) {

			while ((line = reader.readLine()) != null) {
				lines.add(line);

				if (line.equals("import javax.portlet.Portlet;")) {
					StringBuilder sb = new StringBuilder();

					sb.append(
						"import java.io.IOException;\n"
					).append(
						"import javax.portlet.PortletException;\n"
					).append(
						"import javax.portlet.RenderRequest;\n"
					).append(
						"import javax.portlet.RenderResponse;\n"
					);

					lines.add(sb.toString());
				}

				if (line.equals(
						"public class HelloworldPortlet extends MVCPortlet {"
					)) {

					StringBuilder sb = new StringBuilder();

					sb.append(
						"public void doView(\nRenderRequest renderRequest,"
					).append(
						" RenderResponse renderResponse)\n"
					).append(
						"throws IOException, PortletException {\n"
					).append(
						"renderRequest.setAttribute(\n"
					).append(
						"\"foo\", \"bar\");\nsuper.doView(renderRequest, "
					).append(
						"renderResponse);\n}\n"
					);

					lines.add(sb.toString());
				}
			}
		}

		try (Writer writer = new FileWriter(dynamicFile)) {
			for (String string : lines) {
				writer.write(string + "\n");
			}
		}

		File staticFile = new File(
			_projectPath + "/src/main/resources/META-INF/resources/view.jsp");

		String content = new String(Files.readAllBytes(staticFile.toPath()));

		StringBuilder sb = new StringBuilder(content);

		sb.insert(
			content.lastIndexOf("b") + 2,
			"<b><%= renderRequest.getAttribute(\"foo\") %></b>\n");

		Files.write(staticFile.toPath(), sb.toString().getBytes());

		_buildStatus = BladeCLIUtil.execute(_projectPath, "gw", "assemble");

		Assert.assertTrue(
			"Expected Build Successful, but saw: " + _buildStatus,
			!_buildStatus.contains("failed"));

		File buildOutput = new File(
			_projectPath + "/build/libs/helloworld-1.0.0.jar");

		new JMXBundleDeployer().deploy(_helloWorldJarBSN, buildOutput);

		Thread.sleep(1000);

		_webDriver.get(_portletURL.toExternalForm());

		Assert.assertTrue(
			"Portlet was not deployed", isVisible(_helloWorldPortlet));
		Assert.assertTrue(
			_portletTitle.getText(),
			_portletTitle.getText().contentEquals("helloworld Portlet"));
		Assert.assertTrue(
			_portletBody.getText(),
			_portletBody.getText().contentEquals(
				"Hello from helloworld JSP!bar"));
	}

	protected boolean isVisible(WebElement webelement) {
		WebDriverWait webDriverWait = new WebDriverWait(_webDriver, 60);

		try {
			webDriverWait.until(ExpectedConditions.visibilityOf(webelement));

			return true;
		}
		catch (org.openqa.selenium.TimeoutException te) {
			return false;
		}
	}

	private static File _modulesDir;
	private static String _buildStatus;
	private static String _helloWorldJarBSN = "helloworld";
	private static File _projectPath;

	@FindBy(xpath = "//div[contains(@id,'_Helloworld')]")
	private WebElement _helloWorldPortlet;

	@FindBy(xpath = "//div[contains(@id,'_Helloworld')]//..//p")
	private WebElement _portletBody;

	@FindBy(xpath = "//div[contains(@id,'_Helloworld')]//..//h2")
	private WebElement _portletTitle;

	@PortalURL("Helloworld")
	private URL _portletURL;

	@Drone
	private WebDriver _webDriver;

}