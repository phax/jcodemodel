# Extending JCM with plugin generator

JCM ships a Maven plugin that runs a code generator at build time and writes the resulting sources into your project.

## JCodeModel maven plugin

This maven plugin is proposed as a part of the project. It allows to be executed at specific maven phases ; however it does not generate data by itself, as it needs to be specified a generator. For example, the [helloworld plugin example](../examples/plugins/helloworld/pom.xml) extensively configures that plugin in various ways.

The [plugin code](../plugin/plugin/src/main/java/com/helger/jcodemodel/plugin/maven/GenerateSourceMojo.java) does much of the leg-work to load a configured resource, load the configured generator - or default one from the plugin dependency - then call that generator and export the produced JCodeModel into a target directory.

It knows several generic settings that can be used when generating the data. For example, the root package of all classes, the target directory, the source to load.
Any additional generator-specific configuration can be passed as the `params` plugin configuration.

For example, in the HelloWorld example,

```xml
<configuration>
	<generator>com.helger.jcodemodel.plugin.generators.helloworld.HelloWorldGenerator2</generator>
	<outputDir>src/generated/java2</outputDir>
	<params>
		<name>Hello2</name>
		<value>world2</value>
	</params>
</configuration>
```

This configuration forces a specific generator (instead of the default one provided by the HelloWorld Generator) ; then sets the output to be specific one (instead of default `src/generated/java`) ; then transmits the map `name=Hello2, value=world2` to the generator when the plugin is called. 

### Extending AbstractFlatStructureGenerator

This class, available in the plugin module, generates JCM data from a hierarchy of simpler classes. If your generator extends it, it is limited in the classes it can generate but a huge part of code is already present.

This is the class that the yaml, json and csv generators are based on.

## New generator in this project

This part focuses on how to add a new generator in this project, generic enough to be used by other projects. 

### Creating the generator itself

To create a new Generator, you must
1. create a new module in the `plugin/generators` directory
2. add it to the [generators pom](../plugin/generators/pom.xml) modules list
3. In your module, create a class that implements `ICodeModelBuilder` and is annotated with `@JCMGen`
4. In that class, implement your generator logic in the `void build (JCodeModel model, @Nullable InputStream source) throws JCodeModelException` method.
5. if needed, specify how it's configured by overriding the `void configure (@NonNull final Map <String, String> params)` method 

The [annotation processor](../plugin/plugin/src/main/java/com/helger/jcodemodel/plugin/maven/generators/JCMGenProcessor.java) will automatically generate the resources required by the plugin on the maven install phase.

You still need to test your generator, to be sure nothing changes too much. 

### Creating the generator tests 

Once your generator module is done,

1. create a new project in the `examples/plugins` module
2. add it to the [plugin examples](../examples/plugins/pom.xml) modules list
3. add your invocation of the plugin, with your generator as its dependency.

This last part would result in something like

```xml
<project>
	<build>
		<plugins>
			<plugin>
				<groupId>com.helger.jcodemodel</groupId>
				<artifactId>jcodemodel-maven-plugin</artifactId>
				<dependencies>
					<dependency>
						<groupId>com.helger.jcodemodel.plugin.generators</groupId>
						<artifactId>mygenerator</artifactId>
						<version>${project.version}</version>
					</dependency>
				</dependencies>
```

several executions can be configured, with various generators and/or configurations. If you use several dependencies, you need to specify the actual generator class in each execution.

## Creating a generator in your own project

Assuming you want to also use your generator in that project. In that case it's a bit more complex.

First, you need to have your project be a multi-module project, that means the root module must be a pom packaging. 

Then you need to decide where you will put the generator module. The maven module the generator will be put under is the *base* module, while the main module of your project is the *root* module.

Then create three modules under the *base* one : 

 - *commons* lists the dependencies used by the generator that also need to be imported in the generated code.
 - *generator* contains the code of the generator to be used by the plugin
 - *generated* contains the result of the execution of the plugin.

Don't forget to add them to the *base* modules list !

### The *commons* module

This module is important, as typically if your generator create instances of a specific class, this class must be known by the generator and by the project using the generated module.

So all it needs to know is which dependencies are required for plugin execution, and for generated classes

### The *generator* module

This modules describes a JCM generator.

Be sure to import the *commons* module. Then add the following to the pom : 

```xml
	<dependencies>
		<dependency>
			<groupId>com.helger.jcodemodel</groupId>
			<artifactId>jcodemodel-maven-plugin</artifactId>
			<scope>provided</scope>
		</dependency>
	</dependencies>
	<build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-compiler-plugin</artifactId>
				<configuration>
					<annotationProcessorPaths>
						<path>
							<groupId>com.helger.jcodemodel</groupId>
							<artifactId>jcodemodel-maven-plugin</artifactId>
							<version>${jcodemodel.version}</version>
						</path>
					</annotationProcessorPaths>
					<annotationProcessors>
						<annotationProcessor>com.helger.jcodemodel.plugin.maven.generators.JCMGenProcessor</annotationProcessor>
					</annotationProcessors>
				</configuration>
			</plugin>
		</plugins>
	</build>
```

 - the dependency assumes you defined the version in your *root* module. If that's not the case, add a `<version>${jcodemodel.version}</version>` line to it - assuming you specified `jcodemodel.version` as a property of the *root* module. If not, add one ! Or even a property of the *base* module.
 - the compiler plugin configuration is required otherwise the generator won't get its file processed correctly, and won't be loaded by the plugin. this would result in either a failure `Annotation processor 'com.helger.jcodemodel.plugin.maven.generators.JCMGenProcessor' not found` in the *generator* execution, or a `could not load the generator class` in the *generated* execution.

### The *generated* module

In this module, in the pom, you need to 

 - add the dependency to *commons*
 - configure plugins to import the generated dir, if that's the one you use, and delete it upon clean :

```xml
	<build>
		<plugins>
			<plugin>
				<artifactId>maven-clean-plugin</artifactId>
				<configuration>
					<filesets>
						<fileset>
							<directory>src/generated/java</directory>
						</fileset>
					</filesets>
				</configuration>
			</plugin>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>build-helper-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>add-source</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>add-source</goal>
						</goals>
						<configuration>
							<sources>
								<source>src/generated/java</source>
							</sources>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
```

 - configure the JCM plugin to generate the code using the generator : 

```xml
		<plugin>
			<groupId>com.helger.jcodemodel</groupId>
			<artifactId>jcodemodel-maven-plugin</artifactId>
			<configuration>
			  <!-- add here the configuration shared by all executions, if any -->
			</configuration>
			<executions>
				<execution>
					<id>generate-source</id>
					<goals>
						<goal>generate-source</goal>
					</goals>
					<configuration>
						<source>myschemaurl</source><!-- replace with yours -->
					</configuration>
				</execution>
			</executions>
			<dependencies>
				<dependency>
					<groupId>my.project.base</groupId><!-- replace with yours -->
					<artifactId>generator</artifactId>
					<version>${project.version}</version>
				</dependency>
			</dependencies>
		</plugin>
```

Don't forget to use the correct url/file as well as your correct groupId

Then you can try the execution of the plugin with `mvn clean install -pl :generated --am`

You can limit the execution to a specific profile, say *generate*. In that case you need to add `-P generate` to your execution, when you specifically want to apply the plugin. In that case, the clean plugin should also be put in that profile to avoid losing the classes when a `mvn clean install` is used.

In that case, the pom would look like


```xml
	<build>
		<plugins>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>build-helper-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>generate-source</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>add-source</goal>
						</goals>
						<configuration>
							<sources>
								<source>src/generated/java</source>
							</sources>
						</configuration>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
	<profiles>
		<profile>
			<id>generate</id>
			<build>
				<plugins>
					<plugin>
						<artifactId>maven-clean-plugin</artifactId>
						<configuration>
							<filesets>
								<fileset>
									<directory>src/generated/java</directory>
								</fileset>
							</filesets>
						</configuration>
					</plugin>
					<plugin>
						<groupId>com.helger.jcodemodel</groupId>
						<artifactId>jcodemodel-maven-plugin</artifactId>
						<configuration>
						  <!-- add here the configuration shared by all executions, if any -->
						</configuration>
						<executions>
							<execution>
								<id>generate-source</id>
								<goals>
									<goal>generate-source</goal>
								</goals>
								<configuration>
									<source>myschemaurl</source><!-- replace with yours -->
								</configuration>
							</execution>
						</executions>
						<dependencies>
							<dependency>
								<groupId>my.project.base</groupId><!-- replace with yours -->
								<artifactId>generator</artifactId>
								<version>${project.version}</version>
							</dependency>
						</dependencies>
					</plugin>
				</plugins>
			</build>
		</profile>
	</profiles>
```

