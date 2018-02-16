package com.tech11.docu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReplacerDirectoryMojoTest {

	File testDir;

	@BeforeEach
	public void init() throws IOException {
		File jarSource = new File(this.getClass().getResource("/test-javadoc.jar").getFile());

		String testDirPath = jarSource.getParentFile().getParent() + File.separator + "TEST-"
				+ System.currentTimeMillis() + File.separator;
		testDir = new File(testDirPath);
		testDir.mkdir();

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(jarSource))) {
			ZipEntry zipEntry = zis.getNextEntry();
			byte[] buffer = new byte[1024];
			while (zipEntry != null) {
				if (!zipEntry.isDirectory()) {
					File newFile = new File(testDirPath + zipEntry.getName());

					if (!newFile.getParentFile().isDirectory())
						newFile.getParentFile().mkdirs();

					try (FileOutputStream fos = new FileOutputStream(newFile)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
					}
				}
				zipEntry = zis.getNextEntry();
			}
		}
	}

	@Test
	public void testRegEx() throws MojoExecutionException {
		ReplacerDirectoryMojo rdm = new ReplacerDirectoryMojo();

		rdm.sourceDir = testDir;
		rdm.fileSuffix = "html";
		rdm.docuRepoPath = this.getClass().getResource("/docu-repo.html").getFile();

		rdm.execute();
		Assertions.assertEquals(4, rdm.replacedFileNames.size());
		Assertions.assertEquals(1, rdm.replacedFileNames.stream()
				.filter(fn -> fn.contains("ARContractVersion")).count());

	}

}
