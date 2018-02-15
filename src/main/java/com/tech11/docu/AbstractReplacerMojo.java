package com.tech11.docu;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public abstract class AbstractReplacerMojo extends AbstractMojo {

	static final Pattern PATTERN = Pattern.compile("#tech11-doc-lookup #(\\w*).*");

	@Parameter(defaultValue = "html")
	String fileSuffix;

	@Parameter(required = true)
	String docuRepoPath;

	Document docuRepoDoc;

	List<String> replacedFileNames = new ArrayList<>();

	public void execute() throws MojoExecutionException {
		try {
			initRepoDoc();
			doExecute();
		} catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	abstract void doExecute() throws IOException;

	void initRepoDoc() throws MojoExecutionException {
		try {
			getLog().info("Use as docu shared repository: " + docuRepoPath);

			if (!docuRepoPath.contains("//")) {
				docuRepoPath = "file:///" +
						docuRepoPath; // no URL prefix. use file
				getLog().info("change docuRepoPath to: " + docuRepoPath);
			}
			URL sourceUrl = new URL(docuRepoPath);
			if (!new File(sourceUrl.toURI()).exists())
				throw new MojoExecutionException("Docu Repo is not available (" + sourceUrl + ")");

			final String sourceContent;
			try (BufferedReader buffer = new BufferedReader(new InputStreamReader(sourceUrl.openStream()))) {
				sourceContent = buffer.lines().collect(Collectors.joining("\n"));
			}

			docuRepoDoc = Jsoup.parse(sourceContent);
		} catch (IOException | URISyntaxException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	Replacement replaceContent(String source) {
		boolean modified = false;
		String result = source;
		// replace doc-lookup content
		Matcher m = PATTERN.matcher(result);
		while (m.find()) {
			modified = true;
			String key = m.group(1);
			Element e = docuRepoDoc.getElementById(key);
			String tagContent = e.html();
			result = m.replaceAll(tagContent);
		}

		return new Replacement(result, modified);
	}

}
