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

	String testJarSource;

	@BeforeEach
	public void init() throws IOException {
		File jarSource = new File(this.getClass().getResource("/test-javadoc.jar").getFile());
		Path source = Paths.get(jarSource.getAbsolutePath());
		testJarSource = jarSource.getParent() + File.separator + "TEST-" + jarSource.getName();
		Path destination = Paths.get(testJarSource);

		Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
	}

	@Test
	public void testRegEx() throws MojoExecutionException {
		ReplacerJarMojo rjm = new ReplacerJarMojo();

		rjm.jarFile = new File(testJarSource);
		rjm.fileSuffix = "html";
		rjm.docuPattern = "#docu-lookup:";
		rjm.docuRepoPath = this.getClass().getResource("/docu-repo.html").getFile();
		
		rjm.execute();
		Assertions.assertEquals(4, rjm.replacedFileNames.size());
	}

}
