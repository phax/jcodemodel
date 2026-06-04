## Extending JCM with plugin generator

### JCodeModel maven plugin

The plugin is part of the project. However it does not generate data by itself, it needs to be specified a generator. For example, the [helloworld plugin example](../examples/plugins/helloworld/pom.xml) extensively configures that plugin in various ways.

The [plugin](../plugin/plugin/src/main/java/com/helger/jcodemodel/plugin/maven/GenerateSourceMojo.java) does much of the leg-work to load a configured resource, load the configured generator - or default one if none is specified but present as dependency of the plugin - then call that generator and export the produced JCodeModel into a target directory.

It contains several generic settings that can be used when generating the data. For example, the root package of all classes, the target directory, the source to load.
Any more genrator-specific configuration can be passed as the `params` plugin configuration. For example, in the HelloWorld example,

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

forces a specific generator (instead of the default one provided by the HelloWorld Generator) ; then sets the output to be specific one (instead of default `src/generated/java`) ; then transmits the map `name=Hello2, value=world2` to the generator when the plugin is called.  

### Creating a generator

To create a new Generator, you must
1. create a new project in the the `plugin/generators` directory
2. add it to the [generators pom](../plugin/generators/pom.xml) modules list
3. create a class that implements `ICodeModelBuilder` and is annotated with `@JCMGen`
4. implement your generator logic in the `void build (JCodeModel model, @Nullable InputStream source) throws JCodeModelException` method.
5. if needed, specify how it's configured by overriding the `void configure (@NonNull final Map <String, String> params)` method 

The [annotation processor](../plugin/plugin/src/main/java/com/helger/jcodemodel/plugin/maven/generators/JCMGenProcessor.java) will automatically generate the resources required by the plugin on the maven install phase.

You still need to test your generator, to be sure nothing changes too much

### Testing a generator 

Once the generator module is done, you create a new project in the `examples/plugins` module, add it to the [plugin examples](../examples/plugins/pom.xml) modules list, and add your invocation of the plugin, with the generator as its dependency.

This would be something like

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

several executions can be configured, with various generators and/or configurations.