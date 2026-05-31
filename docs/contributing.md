## Contributing

### Formatting

Pull requests must follow my personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md)

#### Tabs vs spaces

This project uses double-space for indentation. If you want to use tabs, you can ask git to modify the files when commiting them and when pulling them. A [specific script](../sh/tabspaces) makes that change, run it from the root project.


What this script does  : 

 - create the file .git/info/attributes with `*.java filter=tabspace` . This will tell git to apply the script tabspace on the *.java files
 - run `git config filter.tabspace.clean 'expand --tabs=2 --initial'` to ask git to replace tabs with two spaces on commit of *.java files.
 - run `git config filter.tabspace.smudge 'unexpand --tabs=2 --first-only'` to request git to replace double spaces with tabs when checking a *.java file out.


#### Eclipse

For eclipse, a formatter xml and a cleanup xml are present in the meta/formatter/eclipse/ directory. You can load them from the "project properties > java code style" settings. Check "Enable project specific settings", then load them.

NOTE : you also need to change the save actions to make them meet the clean up actions. Save actions are done even when they are not present in the clean up.


### Testing

New features, as well as bug fixes, are expected to test the minimum amount of use cases to ensure their main usage remains correct.
For example, if you find a bug in a specific case, then that specific case must be tested against in the PR fixing it.
This is important both for validating your PR, but also to ensure new modifications won't break the existing features

Disabling an existing test must be explained (typically no more relevant), at least in the commit or in the PR.

There are three main ways to add tests :
 - checking your own class behavior and the generated **file** content,
 - checking the generated **class** content and behavior,
 - checking your plugin generator behavior.

#### In the main module

You can add usual unit tests in the [main module's test dir](../jcodemodel/src/test/java).

Those are useful to check the behavior of specific parts of the projects, as well as the expected file content for a constructed JCM , typically using [test utils](../jcodemodel/src/test/java/com/helger/jcodemodel/util/CodeModelTestsHelper.java)

Implementation of parsers, validations, a well as code generation that does not persist at runtime (like javadocs, formatting, etc.) are expected to use this method.

Note that the helper class allows to compile a JCM in memory, however using the generated class can be cumbersome since you need to use reflect, unless you can cast it to a known interface.
The next method allows easier manipulation, plus it permits to visually check the generated class files since they are exported and put in git. Therefore any later change in generated files can be tracked down to its commit.

#### In the jcodemodeltests module

[This module](../jcodemodeltests) uses a specific architecture : 

1. generating classes should be annotated with `@TestJCM` and contain public methods that have a JCM and/or a JPackage parameter(s), or produce their own JCM. The convention is to end such a class with `TestGen` and place them in their own feature package. The method can be static; if not, a new instance is generated for each generating method. 
2. those classes are parsed during the generate-test phase and the resulting (or requested) JCM is then exported in the `src/generated/javatest` dir. You can run the [GenerateTestFiles](../jcodemodeltests/src/main/java/com/helger/jcodemodel/compile/annotation/GenerateTestFiles.java) in your IDE to generate them manually.
3. You can then add test classes in the usual `src/test/java` dir, that rely on those generated classes to check their behaviour and content. The convention is to place the test clas in the same package and with same start as the generating one, ending with `Test`.

General convention is as such, for feature Feat : generat**ing** is `jcodemodel/tests/feat/FeatTestGen.java` ; generat**ed** should be named eg `jcodemodel/tests/feat/FeatExample1.java` ; **testing** class should be `jcodemodel/tests/feat/FeatTest.java`

#### Testing your plugin's generator

A generator generates a JCM that the plugin will export when requested.

 - The generator module should be in the [../plugin/generators] submodule, with a module name starting with `GEN ` (in its pom) ;
 - The testing module should be in the [../examples/plugins] submodule, with a module name starting with `XPL Generator ` .

For example, the [HelloWorld generator](../plugin/generators/helloworld/pom.xml) and its [HelloWorld example](../examples/plugins/helloworld/pom.xml) modules.

The testing module should not rely on internet data, as this can be an issue when remote host is down.
With [correct configuration](../examples/plugins/helloworld/pom.xml) the plugin will apply the generator and produce the classes in `src/generated/java` , allowing the usual unit tests in that module.