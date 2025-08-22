package com.helger.jcodemodel.plugin.maven;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate-source", threadSafe = true)
public class GenerateSourceMojo extends AbstractMojo {

	@Component
	private MavenProject project;

	@Parameter(property = "jcodemodel-outdir")
	private String outputJavaDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// TODO Auto-generated method stub

	}

	protected File javaOutputFolder() {
		if (outputJavaDir == null) {
			return new File(project.getBasedir(), "src/generated/java");
		} else if (outputJavaDir.startsWith("/")) {
			return new File(outputJavaDir);
		} else {
			return new File(project.getBasedir(), outputJavaDir);
		}
	}

}
