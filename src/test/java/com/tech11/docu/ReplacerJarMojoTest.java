package com.tech11.docu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplacerJarMojoTest {

	static final String PATTERN = "#tech11-doc-lookup #(\\w*).*";
	
	
	String testJarSource;
	
	@BeforeEach
	public void init() throws IOException {
		System.out.println("init TEST");
		File jarSource = new File(this.getClass().getResource("/test-javadoc.jar").getFile());
		Path source = Paths.get(jarSource.getAbsolutePath());
		testJarSource = jarSource.getParent() + File.separator + "TEST-" + jarSource.getName();
		System.out.println(testJarSource);
	    Path destination = Paths.get(testJarSource);
	 
	    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
	}

	@Test
	public void testRegEx() throws MojoExecutionException {
		ReplacerJarMojo rjm = new ReplacerJarMojo();

		rjm.jarFile = new File(testJarSource);
		rjm.fileSuffix = "html";
		rjm.docuRepoPath = this.getClass().getResource("/docu-repo.html").getFile();

		rjm.execute();
		Assertions.assertEquals(4, rjm.replacedFileNames.size());
	}

}
