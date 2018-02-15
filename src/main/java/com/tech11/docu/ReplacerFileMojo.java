package com.tech11.docu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@Mojo(name = "replace")
public class ReplacerFileMojo extends AbstractMojo {

	@Parameter
	File sourceDir;

	@Parameter(defaultValue = "html")
	String fileSuffix;

	@Parameter(required = true)
	String sourcePath;

	Document sourceDoc;

	static final Pattern PATTERN = Pattern.compile("#tech11-doc-lookup #(\\w*).*");

	public void execute() throws MojoExecutionException {
		getLog().info("jarFile: " + sourceDir.getAbsolutePath());
		getLog().info("docuRepoPath: " + sourcePath);

		try {

			if (!sourcePath.contains("//")) {
				sourcePath = "file:///" + sourcePath; // no URL prefix. use file
				getLog().info("change docuRepoPath to: " + sourcePath);
			}
			URL sourceUrl = new URL(sourcePath);
			getLog().info("sourceUrl: " + sourceUrl.toString());
			final String sourceContent;
			try (BufferedReader buffer = new BufferedReader(new InputStreamReader(sourceUrl.openStream()))) {
				sourceContent = buffer.lines().collect(Collectors.joining("\n"));
			}
			getLog().info("sourceContent");
			getLog().info(sourceContent);

			sourceDoc = Jsoup.parse(sourceContent);

			runDirectory(sourceDir.getAbsolutePath());
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}

	}

	void runDirectory(String sourcePath) throws IOException {
		getLog().info("work path " + sourcePath);

		for (File f : new File(sourcePath).listFiles()) {
			if (f.isFile() && f.getName().endsWith(fileSuffix)) {
				getLog().info("work file: " + f.getAbsolutePath());

				// read file
				String content = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));

				// replace doc-lookup content
				Matcher m = PATTERN.matcher(content);
				while (m.find()) {

					String key = m.group(1);
					Element e = sourceDoc.getElementById(key);
					String tagContent = e.html();
					content = m.replaceAll(tagContent);
				}

				// write file
				Files.write(Paths.get(f.getAbsolutePath()), content.getBytes());

			} else if (f.isDirectory()) { // call recursive
				runDirectory(f.getAbsolutePath());
			}
		}
	}

}
