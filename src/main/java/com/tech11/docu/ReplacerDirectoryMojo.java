package com.tech11.docu;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "dir")
public class ReplacerDirectoryMojo extends AbstractReplacerMojo {

	@Parameter(required = true)
	File sourceDir;

	@Override
	void doExecute() throws IOException {
		getLog().info("Start replacement in directory: " + sourceDir.getAbsolutePath());

		runDirectory(sourceDir);

		getLog().info(replacedFileNames.size() + " files were replaced.");
	}

	void runDirectory(File dir) throws IOException {

		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(fileSuffix)) {
				// read file
				String content = new String(Files.readAllBytes(Paths.get(f.getAbsolutePath())));

				Replacement replacement = replaceContent(content);
				if (replacement.isModified()) {
					replacedFileNames.add(f.getAbsolutePath());
					// write file
					Files.write(Paths.get(f.getAbsolutePath()), replacement.getContent().getBytes());
					getLog().debug("replaced: " + f.getAbsolutePath());
				}
			} else if (f.isDirectory()) { // call recursive
				runDirectory(f);
			}
		}
	}

}
