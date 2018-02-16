package com.tech11.docu;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "jar")
public class ReplacerJarMojo extends AbstractReplacerMojo {

	@Parameter(required=true)
	File jarFile;

	@Override
	void doExecute() throws IOException {
		getLog().info("Start replacement in jarFile: " + jarFile.getAbsolutePath());
		
		runJarFile(jarFile);
		
		getLog().info(replacedFileNames.size() + " files were replaced.");
	}

	void runJarFile(File jarFile) throws IOException {
		Path zipFilePath = Paths.get(jarFile.getAbsolutePath());

		try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, null); // take care for the order
				ZipFile zipFile = new ZipFile(jarFile.getAbsolutePath())) { // zipFile has to be closed first
			zipFile.stream()
					.filter(ze -> !ze.isDirectory())
					.filter(ze -> ze.getName().endsWith(fileSuffix))
					.forEach(ze -> {

						Path htmlFileInsideZip = fs.getPath(ze.getName());

						try {
							String content = new String(Files.readAllBytes(htmlFileInsideZip));

							Replacement replacement = replaceContent(content);
							if (replacement.isModified()) {
								replacedFileNames.add(htmlFileInsideZip.toString());
								// write file
								Files.delete(htmlFileInsideZip);
								Files.write(htmlFileInsideZip, replacement.getContent().getBytes());
								getLog().debug("replaced: " + ze.getName());
							}

						} catch (IOException e) {
							throw new UncheckedIOException(e);
						}
					});
		}

	}

}
