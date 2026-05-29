# jcodemodel

<!-- ph-badge-start -->
[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/com.helger.jcodemodel/jcodemodel-parent-pom/badge.svg)](https://maven-badges.sml.io/sonatype-central/com.helger.jcodemodel/jcodemodel-parent-pom/)
[![javadoc](https://javadoc.io/badge2/com.helger/jcodemodel/javadoc.svg)](https://javadoc.io/doc/com.helger/jcodemodel)
<!-- ph-badge-end -->

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.
That of course implies, that this artefact cannot directly be used with JAXB, since the configuration of 
this would be very tricky.

A site with the links to the [API docs](http://phax.github.io/jcodemodel/) etc. is available.

## Maven usage

Add the following to your pom.xml to use this artifact (where `x.y.z` denotes the version):

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>jcodemodel</artifactId>
  <version>x.y.z</version>
</dependency>
```

# News and noteworthy

v4.2.1 - 2026-05-29
* Added support for annotations with parameters on type annotations and fixed `@since` tags. See [#130](https://github.com/phax/jcodemodel/pull/130) - thx @joelittlejohn
* Added support for Java text blocks via [#145](https://github.com/phax/jcodemodel/pull/145) and [#147](https://github.com/phax/jcodemodel/pull/147), including a `keepWhiteSpaces` option - thx @glelouet
* Generated test sources now carry an `@Generated` annotation [#149](https://github.com/phax/jcodemodel/pull/149) - thx @glelouet
* Added proper license headers to the `generated/javatests` sources [#139](https://github.com/phax/jcodemodel/pull/139) - thx @glelouet

v4.2.0 - 2026-05-13
* Removed OSGI bundling
* Added support for Java `record` types. Fixes [#98](https://github.com/phax/jcodemodel/issues/98) via [#126](https://github.com/phax/jcodemodel/pull/126) - thx @joelittlejohn
* Added support for annotation target `TYPE_USE`. Fixes [#50](https://github.com/phax/jcodemodel/issues/50) via [#127](https://github.com/phax/jcodemodel/pull/127) - thx @joelittlejohn
* Added `JDefinedClass.isRecord ()` and a mutable accessor `recordComponentsMutable ()`; `recordComponents ()` now returns an immutable list
* Added `JExpr.ref (JRecordComponent)` and `JExpr.refthis (JRecordComponent)` for nicer record component references
* Added `JBlock._throw (AbstractJClass, IJExpression...)` overload for throwing an exception with constructor arguments
* Fixed JavaDoc reference to use `JCMWriter` instead of the deprecated `cm.build (...)`. See [#135](https://github.com/phax/jcodemodel/issues/135)

v4.1.0 - 2025-11-16
* Updated to ph-commons 12.1.0
* Using JSpecify annotations
* Added Maven plugin to generate Java code from CSV, JSON or YAML. Thanks a million to @glelouet for providing all of the great work

v4.0.0 - 2025-08-25
* Requires Java 17 as the minimum version
* Using `ph-commons` as a compile dependency for common stuff
* Moved all exceptions to `exceptions` package
* Included [PR #96]](https://github.com/phax/jcodemodel/pull/96) from @glelouet on copying the whole JCodeModel

v3.4.1 - 2022-01-20
* Extended `JDefinedClass` to make enum constants accessible

v3.4.0 - 2020-05-25
* Added special top-level construct `JResourceDir` to represent pure resource directories ([issue #74](https://github.com/phax/jcodemodel/issues/74) from @guiguilechat)
* Added new class `JCodeModelException` as the base class for `JClassAlreadyExistsException` and the new class `JResourceAlreadyExistsException`
* Existing APIs were changed to throw `JCodeModelException` instead of `JClassAlreadyExistsException`
* `JCNameUtilities.getFullName` works with classes in the default package
* Extended `JCodeModel` with `(get|set)FileSystemConvention` to make the creation more flexible ([also issue #74](https://github.com/phax/jcodemodel/issues/74) from @guiguilechat)
* Added mutable overloads to methods that only return an unmodifiable collection ([issue #86](https://github.com/phax/jcodemodel/issues/86))
* Fixed an issue with generating generics from anonymous classes ([issue #84](https://github.com/phax/jcodemodel/issues/84))

v3.3.0 - 2019-11-24
* Added check for package names so that no invalid package names can be created ([issue #70](https://github.com/phax/jcodemodel/issues/70) from @guiguilechat)
* Added check to avoid creating classes existing in the "java.lang" package ([issue #71](https://github.com/phax/jcodemodel/issues/71) from @guiguilechat)
* `JLambdaMethodRef` now works with arbitrary expressions as the left hand side

v3.2.4 - 2019-07-15
* Made class `JavaUnicodeEscapeWriter` publicly accessible
* Extended enum constant ref API ([issue #68](https://github.com/phax/jcodemodel/issues/68) from @guiguilechat)

v3.2.3 - 2019-03-31
* Extended `JTryBlock` API to have more control.
* Added support for `try-with-resources` support ([issue #67](https://github.com/phax/jcodemodel/issues/67) from @gmcfall)

v3.2.2 - 2019-02-25
* Using `jsr305` instead of `annotations` in POM ([issue #66](https://github.com/phax/jcodemodel/issues/66) from @jjYBdx4IL)

v3.2.1 - 2019-01-23
* Added `var` as reserved word
* Made `JReturn` constructor public
* Added `JInvocation._this` static method
* Added `IJExpression.castTo` method
* Added support to create `final` variables in `for each` loops
* `JExpr.dotClass` now takes `AbstractJType` and not just `AbstractJClass`
* Made constructors of subclasses of `IJStatement` public
* No line breaks for annotations to parameters
* Put each method parameter on a separate line if more than 3 parameters are present

v3.2.0 - 2018-10-20
* Introduced class `JCMWriter` that should be used to emit the outgoing Java files. This replaces `codemodel.build` and offers a more consistent API. Most existing method remain existing and deprecated and just forward to `JCMWriter`.
    * Instead of `cm.build (...)` use `new JCMWriter (cm).build (...)` 
* Extracted `IJFormatter` interface for better separation of concerns. `JFormatter` was moved to a sub-package
* `ProgressCodeWriter` no longer needs an explicit `PrintStream` but a `ProgressCodeWriter.IProgressTracker` instead.
* Default charset for Java classes is now `UTF-8`.
* Added new `JAnnotationUse` method overloads that automatically pass `value` as the annotation parameter name ([issue #64](https://github.com/phax/jcodemodel/issues/64)) 

v3.1.0 - 2018-08-22
* Added ` AbstractJType._new()`
* Change return types of special `JBlock` methods to `void` to avoid chaining ([issue #62](https://github.com/phax/jcodemodel/issues/62)) - incompatible change!
* Added new `JExpr.invokeThis` and `JExpr.invokeSuper` static methods

v3.0.3 - 2018-06-12
* Improved API access to inner classes ([issue #60](https://github.com/phax/jcodemodel/issues/60))
* Changed order of emitted modifiers (`final static` -> `static final`) 
* Flush needed when writing resources fixed ([issue #61](https://github.com/phax/jcodemodel/issues/61) from @fbaro)

v3.0.2 - 2018-04-11
* Fixed method resolution using direct class references ([issue #58](https://github.com/phax/jcodemodel/issues/458))
* Added some additional `JInvocation.arg...` sanity methods
* Enum constant argument list is now accessible

v3.0.1 - 2017-10-25
* Added explicit support for invoking `super` - thx to @heruan for pointing this out
* Added possibility to create a lambda reference from an invocation ([issue #56](https://github.com/phax/jcodemodel/issues/56) and [PR #57](https://github.com/phax/jcodemodel/pull/57) from @heruan)

v3.0.0 - 2017-08-06
* Requires Java 8
* Reworked [#41](https://github.com/phax/jcodemodel/issues/41) so that it is finally working in all cases
* Add option for classes to not be imported ([issue #51](https://github.com/phax/jcodemodel/issues/51))
* Fixed extra semicolon on Lambdas ([issue #53](https://github.com/phax/jcodemodel/issues/53))

v2.8.6  2016-07-19
* added PR ([issue #49](https://github.com/phax/jcodemodel/issues/49))

v2.8.5 - 2016-05-13
* improved comment handling (issue #47)
* improved API checks (issue #45)
* extended API (issue #46)

v2.8.4 - 2016-04-25
* Enum values in switch statements are no longer fully qualified (issue #41)
* fixed generation of narrowed classes without parameters (as in `HashMap<>`)
* added support for `strictfp` keyword.

v2.8.3 - 2016-02-26
* Added support for single line comments in blocks
* improved generation of Lambdas

v2.8.2 - 2016-01-19
* Customizable new line string and character set
* extensions by @sviperll

v2.8.1 - 2015-12-03
* Extensions by @sviperll

v2.8.0 - 2015-10-12
* Requires Java 1.6
* fixed potential double imports
* added virtual blocks
* integrated [sviperll](https://github.com/sviperll)'s [metachicory](https://github.com/sviperll/chicory/tree/master/metachicory)

v2.7.11 - 2015-09-24
* Bugfix release
* removed half done CSE implementation (issue #18)
* improved handling of directClasses
* added enumConstantReference

v2.7.10 - 2015-06-30
* Synchronized block added
* initial support for lambda expressions

v2.7.9 - 2015-03-19
* Minor extensions for error types

v2.7.8 - 2015-02-05
* Enum constants for annotation parameters

v2.7.7 - 2014-09-17
* mainly API extensions

v2.7.6 - 2014-09-02
* Extended annotation parameter handling API

v2.7.5 - 2014-08-14
* Support for multiple boundaries added (like `T extends AnyClass & Serializable`)

v2.7.4 - 2014-06-12
* Bugfix release

v2.7.3 - 2014-05-23
* Bugfix release

v2.7.2 - 2014-05-21
* now on Maven Central

v2.7.1 - 2014-05-19
* now as OSGi bundle

v2.7.0 - 2014-05-16
* API extensions

v2.6.4 - 2014-04-10

2013-09-23
* Changes from https://github.com/UnquietCode/JCodeModel have been incorporated.

## Contribution

Pull requests must follow my personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md)

### Tabs vs spaces

This project uses double-space for indentation. If you want to use tabs, you can ask git to modify the files when commiting them and when pulling them. A [specific script](sh/tabspaces) makes that chnage, run it from the root project.


What this script does  : 

 - create the file .git/info/attributes with `*.java filter=tabspace` . This will tell git to apply the script tabspace on the *.java files
 - run `git config filter.tabspace.clean 'expand --tabs=2 --initial'` to ask git to replace tabs with two spaces on commit of *.java files.
 - run `git config filter.tabspace.smudge 'unexpand --tabs=2 --first-only'` to request git to replace double spaces with two tabs on checking a *.java file out.


### Eclipse

For eclipse, a formatter xml and a cleanup xml are present in the meta/formatter/eclipse/ directory. You can load them from the "project properties > java code style" settings. Check "Enable project specific settings", then load them.

NOTE : you also need to change the save actions to make them meet the clean up actions. Save actions are done even when they are not present in the clean up.

### Testing

New features, as well as bug fixes, are expected to provide the minimum amount of test cases to ensure their main usage remains correct.
For example, if you find a bug in a specific case, then that specific case must be tested against in the PR fixing it.

Disabling an existing test must be explained (typically no more relevant).

There are three main ways to add tests, one for checking your own class behavior and the **generated file** content, one for checking the generated **class** content and behaviour, and the last for checking your plugin generator behavior.

#### In the main module

You can add usual unit tests in the [main module's test dir](./jcodemodel/src/test/java).

Those are useful to check the behavior of specific parts of the projects, as well as the expected file content for a constructed JCM , typically using [test utils](./jcodemodel/src/test/java/com/helger/jcodemodel/util/CodeModelTestsHelper.java)

Note that the helper class allows to compile a JCML in memory, however using the generated class can be cumbersome since you need to use reflect, unless you can cast it to a known interface.
The next method allows easier manipulation, plus it permits to visualy check the generated class files since they are exported and put in git. Therefore any actual change in generated files can be tracked.

#### In the jcodemodeltests module

[This module](./jcodemodeltests) uses a specific architecture : 

1. generating classes should be annotated with `@TestJCM` and contain public methods that have a JCM and/or a JPackage parameter(s), or produce their own JCM. The convention is to end such a class with `TestGen` and place them in their own feature package. The method can be static; if not, a new instance is generated for each generating method. 
2. those classes are parsed during the generate-test phase and the resulting (or requested) JCM is then exported in the `src/generated/javatest` dir. You can run the [GenerateTestFiles](./jcodemodeltests/src/main/java/com/helger/jcodemodel/compile/annotation/GenerateTestFiles.java) in your IDE to generate them manually.
3. You can then add test classes in the usual `src/test/java` dir, that rely on those generated classes to check their behaviour and content. The convention is to place the test clas in the same package and with same start as the generating one, ending with `Test`.

General convention is as such, for feature Feat : generat**ing** is `jcodemodel/tests/feat/FeatTestGen.java` ; generat**ed** should be named eg `jcodemodel/tests/feat/FeatExample1.java` ; **testing** class should be `jcodemodel/tests/feat/FeatTest.java`

#### Testing your plugin's generator

A plugin generator generates a JCM taht the plugin will export when requested.

The generator module should be in the [./plugin/generators] submodule, with a module name starting with `GEN ` (in its pom) ;
The testing module should be in the [./examples/plugins] submodule, with a module name starting with `XPL Generator ` .
Example are the [HelloWorld generator](./plugin/generators/helloworld)  and its [HelloWorld example](./examples/plugins/helloworld) modules.

The testing module should not rely on internet data, as this can be an issue when remote host is down.
With [correct configuration](./examples/plugins/helloworld/pom.xml) the plugin will apply the generator and produce the classes in `src/generated/java` , allowing the usual unit tests in that module.


---

On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a> |
Kindly supported by [YourKit Java Profiler](https://www.yourkit.com)