## Starting with JCodeModel (JCM)

Before starting, one must now what it can do : JCM is made to generate java code, pre-compiling or at runtime, by a running java program.

### Requirements

Starting requires a Java Development Kit (JDK) at least version 17, maven v3 minimum installed. We strongly recommend an IDE ; and git is always a good idea to manage your code base.
 
Note that Git for windows also embeds a Bash which is required for running the linux scripts on windows. However, if you don't intend to use the scripts nor to manage your codebase testing) then this is not mandatory.

#### Windows

Several links to help you get them :

 - [Java](https://www.java.com/download/manual.jsp)
 - [Maven](https://maven.apache.org/download.cgi)
 - [NetBeans IDE](https://netbeans.apache.org/front/main/download/)
 - [Git](https://git-scm.com/install/windows)

#### Linux

Those are pretty standard. For example Ubuntu can install them with `sudo apt install default-jdk maven netbeans git-all`

#### Maven project setup

Once you have a java project loaded in your IDE, you need to add a dependency to JCM. This is done typically with the following lines in your root pom.xml : 

```xml
<project>
	<dependencies>
		<dependency>
		  <groupId>com.helger</groupId>
		  <artifactId>jcodemodel</artifactId>
		  <version>${jcodemodel.version}</version>
		</dependency>
```

You then need to replace  `${jcodemodel.version}` by the version you intend to use ;  
OR set it as a property (better) typically with

```xml
<project>
	<properties>
		<jcodemodel.version>x.y.z</jcodemodel.version>
```

OR even (recommended) you remove the version line completely and instead import the JCM pom with


```xml
<project>
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>com.helger</groupId>
				<artifactId>jcodemodel-parent-pom</artifactId>
				<version>x.y.z</version>
				<scope>import</scope>
				<type>pom</type>
			</dependency>

	<dependencies>
		<dependency>
		  <groupId>com.helger</groupId>
		  <artifactId>jcodemodel</artifactId>
		</dependency>
```
		
### First program

Let's first make a simple program that generates a new, empty class. With java 25 you can create a fast class `JCMFirstProgram` containing 

```java
void main() throws JCodeModelException, IOException {
	var jcm = new JCodeModel();
	jcm._class("JCMFirstClass");
	File outFile = new File("src/generated/java/");
	outFile.mkdirs();
	new JCMWriter(jcm).build(outFile);
}
```

Then ask your IDE resolve the names for you. This should add imports at the top of the file

If you are using java <25 then you need to use a full class. We made [one already](../jcodemodel/src/test/java/JCMFirstProgram.java)

Let's explain the instructions

 - `var jcm = new JCodeModel();` We need to create a model to represent the classes we want to export. JCodeModel is the class for that model.
 - `jcm._class("JCMFirstClass");` We add a new class in that model. This is a public class by default.
 - `File outFile = new File("src/generated/java/");` we need a directory to export the model. We Choose `src/generated/java` as source files and generated files should be in distinct directories.
 - `outFile.mkdirs();` create the directory if it does not exist.
 - `new JCMWriter(jcm)` we need a writer to write down the model we just created. This allows us to provide exporting options.
 - `.build(outFile);` write the model we created to the specified directory.

### Generating code for a library

Now that you have a main class that produces code, you may want to embed that code in your program. However, the maven building is made in specific steps, and you can't easily compile the same source class and execute it. Instead, you need to have two maven modules, with the first one generating classes, and the second one, **core**, having the first one as dependency and calling its main class.

It's actually possible to have maven do two passes of compiling but this is a bad habit as it blurs the visibility of what is generated, embedded, and can lead to issues with non-deterministic approaches. see [stackoverflow](https://stackoverflow.com/questions/21342342/run-maven-compilation-twice)

#### Regorganising the maven project

1. You need to change your **root** module to a `pom` module (in your root `pom.xml`) , that is an aggregation of other modules. This is the case, for example, for our [root module](../pom.xml) . Note that this means, the root module can't export java code by itself. Regardless, this is a good habit for maven projects, with your root module defining all the dependencies, properties, plugins, etc. .
2. Then you need one **generator** sub module in that root pom to have the class generation. This module will have the java class that we made previously.
3. Your **core** module will have the generating module as a dependency, with scope `optional` ; and a configuration of the `exec-maven-plugin` to call your java main class
4. You also need to tell maven that the `src/generated/java` dir of the **core** module is a source directory

Your **core** pom.xml should now contain : 
 

```xml
<project>
	<dependencie>
		<dependency>
			<groupId>my.project</groupId>
			<artifactId>CodeGenerator</artifactId>
			<version>${project.version}</version>
			<optional>true</optional>
		</dependency>
…
	<build>
		<plugins>
			<plugin>
				<groupId>org.codehaus.mojo</groupId>
				<artifactId>exec-maven-plugin</artifactId>
				<executions>
					<execution>
						<id>generate-classes</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>java</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<mainClass>JCMFirstProgram</mainClass>
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
```

Now, since your **generator** module is not configured to use its `src/generated/java` dir, you can run your main class in your IDE to check the result - but the generated class won't be visible in your **core**, until maven is used to rebuild the project.

### Using the JCM plugin with an existing generator

You can use the JCM plugin, by specifying its generator as dependency and the generator configuration. A generator is basically a parser of a resource into a JCM ; the JCM plugin [when called](../plugin/plugin/src/main/java/com/helger/jcodemodel/plugin/maven/GenerateSourceMojo.java#L105) selects the generator, configures it, generates the JCM, and exports it.

For example, the CSV generator is used [in our examples](../examples/plugins/csv/pom.xml) to generate java source files from either a local file, and/or a complete configuration in the plugin. There are several configurations because there are several features to tests, you don't need that many.

Note that it's possible to use an internet resource for the generator, but doing so should not be bound to a maven phase as a failure with the distant server could lead to building errors.
