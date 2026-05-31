## Starting with JCodeModel (JCM)

Before starting, one must now what it can do : JCM is made to generate java code, pre-compiling or at runtime, by a running java program.

### Requirements

Starting requires a Java Development Kit (JDK) at least version 17, maven v3 minimum installed. We strongly recommend an IDE ; and git to manage your code base.

#### Windows

Several links to help you get them :

 - [Java](https://www.java.com/download/manual.jsp)
 - [Maven](https://maven.apache.org/download.cgi)
 - [NetBeans IDE](https://netbeans.apache.org/front/main/download/)
 - [Git](https://git-scm.com/install/windows)

Note that the Git for windows also embeds a BASH which is very useful for running linux scripts on windows.

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
	new JCMWriter(jcm)
			.build(new File("."));
}
```

Then let your IDE resolve the names for you.

If you are using java <25 then you need to use a full class. We made [one already](../jcodemodel/src/test/java/JCMFirstProgram.java)

Let's explain the instructions

 - `var jcm = new JCodeModel();` We need to create a model to represent the classes we want to export. JCodeModel is the class for that model.
 - `jcm._class("JCMFirstClass");` We add a new class in that model. This is a public class by default.
 - `new JCMWriter(jcm)` we need a writer to write down the model we just created. This allows us to provide exporting options.
 - `.build(new File("."));` write the model we created to the local directory.
 
 