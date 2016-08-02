package com.liferay.blade.tests;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import aQute.bnd.deployer.repository.FixedIndexedRepo;
import aQute.bnd.osgi.Processor;
import aQute.lib.io.IO;

public class BladeCLI {
	public static File createProject (File testDir, String templateName, String bundleName) throws Exception {
		execute("create", "-d", testDir.getAbsolutePath(), "-t", templateName, bundleName);

		File projectPath = new File(testDir + "/" + bundleName);

		return projectPath;
	}

	public static String execute(String... args) throws Exception {
		String bladeclijar = getLatestBladeCLIJar();
		StringBuilder sb = new StringBuilder()
					.append("java -jar ")
					.append(bladeclijar + " ");
					for (String arg : args) {
						sb.append(arg + " ");
					}

		Process process = Runtime.getRuntime().exec(sb.toString());

		process.waitFor();

		InputStream stream = process.getInputStream();
		String output = new String(IO.read(stream));
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

	public static String getLatestBladeCLIJar() throws Exception{
		if (BladeTest.bladeJarPath == null) {
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

			BladeTest.bladeJarPath = cliJar.getCanonicalPath();
		}
		return BladeTest.bladeJarPath;
	}
}
