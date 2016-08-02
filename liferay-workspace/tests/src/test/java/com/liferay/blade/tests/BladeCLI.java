package com.liferay.blade.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.bnd.deployer.repository.FixedIndexedRepo;
import aQute.bnd.osgi.Processor;
import aQute.lib.io.IO;

public class BladeCLI {
	private static File bladeJar;

	public static File createProject (File testDir, String templateName, String bundleName) throws Exception {
		execute("create", "-d", testDir.getAbsolutePath(), "-t", templateName, bundleName);

		File projectPath = new File(testDir + "/" + bundleName);

		return projectPath;
	}
	
	public static String execute(String... bladeArgs) throws Exception {
		return execute(null, bladeArgs);
	}

	public static String execute(File workingDir, String... bladeArgs) throws Exception {

		String bladeCLIJarPath = getLatestBladeCLIJar();

		List<String> command = new ArrayList<>();
		command.add("java");
		command.add("-jar");
		command.add(bladeCLIJarPath);

		for (String arg : bladeArgs) {
			command.add(arg);
		}
		
		Process process = new ProcessBuilder(command.toArray(new String[0])).directory(workingDir).start();

		process.waitFor();

		InputStream stream = process.getInputStream();
		String output = new String(IO.read(stream));
		
		InputStream errorStream = process.getErrorStream();
		String errors = new String(IO.read(errorStream));
		
		assertTrue(errors, errors == null || errors.isEmpty());

		return output;
	}

	public static String installBundle(File file) throws Exception {
		String output = execute("sh", "install", file.toURI().toString());

		String bundleID = output.substring(output.length() -3);

		printFileName = file.getName().split("-")[0];

		System.out.println("Installing " + printFileName);

		if (output.contains("Failed")) {
			throw new Exception(output);
		}

		return bundleID;
	}

	public static String startBundle(String bundleID) throws Exception {
		String output = execute("sh", "start", bundleID);
		System.out.println("Starting " + printFileName);

		if (output.contains("Exception")) {
			throw new Exception(output);
		}
		return output;
	}

	private static String printFileName;

	public static String getLatestBladeCLIJar() throws Exception {
		if (bladeJar == null) {
			String repoPath = new File("build").getAbsolutePath();
			FixedIndexedRepo repo = new FixedIndexedRepo();

			Map<String, String> repoMap = new HashMap<>();
				repoMap.put("name", "index1");
				repoMap.put("locations", "https://liferay-test-01.ci.cloudbees.com/job/liferay-blade-cli/lastSuccessfulBuild/artifact/build/generated/p2/index.xml.gz");
				repoMap.put(FixedIndexedRepo.PROP_CACHE, repoPath);

			repo.setProperties(repoMap);
			repo.setReporter(new Processor());

			File[] files = repo.get( "com.liferay.blade.cli", "[1,2)" );
			File cliJar = files[0];

			bladeJar = cliJar;
		}

		return bladeJar.getCanonicalPath();
	}
}
