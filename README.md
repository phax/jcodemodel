# jcodemodel

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.
That of course implies, that this artefact cannot directly be used with JAXB, since the configuration of 
this would be very tricky.

A site with the links to the [API docs](http://phax.github.io/jcodemodel/) etc. is available.

## Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>jcodemodel</artifactId>
  <version>3.4.1</version>
</dependency>
```

# News and noteworthy

* v3.4.1 - 2022-01-20
    * Extended `JDefinedClass` API to access the contained enum constants
* v3.4.0 - 2020-05-25
    * Added special top-level construct `JResourceDir` to represent pure resource directories ([issue #74](https://github.com/phax/jcodemodel/issues/74) from @guiguilechat)
    * Added new class `JCodeModelException` as the base class for `JClassAlreadyExistsException` and the new class `JResourceAlreadyExistsException`
    * Existing APIs were changed to throw `JCodeModelException` instead of `JClassAlreadyExistsException`
    * `JCNameUtilities.getFullName` works with classes in the default package
    * Extended `JCodeModel` with `(get|set)FileSystemConvention` to make the creation more flexible ([also issue #74](https://github.com/phax/jcodemodel/issues/74) from @guiguilechat)
    * Added mutable overloads to methods that only return an unmodifiable collection ([issue #86](https://github.com/phax/jcodemodel/issues/86))
    * Fixed an issue with generating generics from anonymous classes ([issue #84](https://github.com/phax/jcodemodel/issues/84))
* v3.3.0 - 2019-11-24
    * Added check for package names so that no invalid package names can be created ([issue #70](https://github.com/phax/jcodemodel/issues/70) from @guiguilechat)
    * Added check to avoid creating classes existing in the "java.lang" package ([issue #71](https://github.com/phax/jcodemodel/issues/71) from @guiguilechat)
    * `JLambdaMethodRef` now works with arbitrary expressions as the left hand side
* v3.2.4 - 2019-07-15
    * Made class `JavaUnicodeEscapeWriter` publicly accessible
    * Extended enum constant ref API ([issue #68](https://github.com/phax/jcodemodel/issues/68) from @guiguilechat)
* v3.2.3 - 2019-03-31
    * Extended `JTryBlock` API to have more control.
    * Added support for `try-with-resources` support ([issue #67](https://github.com/phax/jcodemodel/issues/67) from @gmcfall)
* v3.2.2 - 2019-02-25
    * Using `jsr305` instead of `annotations` in POM ([issue #66](https://github.com/phax/jcodemodel/issues/66) from @jjYBdx4IL)
* v3.2.1 - 2019-01-23
    * Added `var` as reserved word
    * Made `JReturn` constructor public
    * Added `JInvocation._this` static method
    * Added `IJExpression.castTo` method
    * Added support to create `final` variables in `for each` loops
    * `JExpr.dotClass` now takes `AbstractJType` and not just `AbstractJClass`
    * Made constructors of subclasses of `IJStatement` public
    * No line breaks for annotations to parameters
    * Put each method parameter on a separate line if more than 3 parameters are present
* v3.2.0 - 2018-10-20
    * Introduced class `JCMWriter` that should be used to emit the outgoing Java files. This replaces `codemodel.build` and offers a more consistent API. Most existing method remain existing and deprecated and just forward to `JCMWriter`.
        * Instead of `cm.build (...)` use `new JCMWriter (cm).build (...)` 
    * Extracted `IJFormatter` interface for better separation of concerns. `JFormatter` was moved to a sub-package
    * `ProgressCodeWriter` no longer needs an explicit `PrintStream` but a `ProgressCodeWriter.IProgressTracker` instead.
    * Default charset for Java classes is now `UTF-8`.
    * Added new `JAnnotationUse` method overloads that automatically pass `value` as the annotation parameter name ([issue #64](https://github.com/phax/jcodemodel/issues/64)) 
* v3.1.0 - 2018-08-22
    * Added ` AbstractJType._new()`
    * Change return types of special `JBlock` methods to `void` to avoid chaining ([issue #62](https://github.com/phax/jcodemodel/issues/62)) - incompatible change!
    * Added new `JExpr.invokeThis` and `JExpr.invokeSuper` static methods
* v3.0.3 - 2018-06-12
    * Improved API access to inner classes ([issue #60](https://github.com/phax/jcodemodel/issues/60))
    * Changed order of emitted modifiers (`final static` -> `static final`) 
    * Flush needed when writing resources fixed ([issue #61](https://github.com/phax/jcodemodel/issues/61) from @fbaro)
* v3.0.2 - 2018-04-11
    * Fixed method resolution using direct class references ([issue #58](https://github.com/phax/jcodemodel/issues/458))
    * Added some additional `JInvocation.arg...` sanity methods
    * Enum constant argument list is now accessible
* v3.0.1 - 2017-10-25
    * Added explicit support for invoking `super` - thx to @heruan for pointing this out
    * Added possibility to create a lambda reference from an invocation ([issue #56](https://github.com/phax/jcodemodel/issues/56) and [PR #57](https://github.com/phax/jcodemodel/pull/57) from @heruan)
* v3.0.0 - 2017-08-06
    * Requires Java 8
    * Reworked [#41](https://github.com/phax/jcodemodel/issues/41) so that it is finally working in all cases
    * Add option for classes to not be imported ([issue #51](https://github.com/phax/jcodemodel/issues/51))
    * Fixed extra semicolon on Lambdas ([issue #53](https://github.com/phax/jcodemodel/issues/53))
* v2.8.6  2016-07-19
    * added PR ([issue #49](https://github.com/phax/jcodemodel/issues/49))
* v2.8.5 - 2016-05-13
    * improved comment handling (issue #47)
    * improved API checks (issue #45)
    * extended API (issue #46)
* v2.8.4 - 2016-04-25
    * Enum values in switch statements are no longer fully qualified (issue #41)
    * fixed generation of narrowed classes without parameters (as in `HashMap<>`)
    * added support for `strictfp` keyword.
* v2.8.3 - 2016-02-26
    * Added support for single line comments in blocks
    * improved generation of Lambdas
* v2.8.2 - 2016-01-19
    * Customizable new line string and character set
    * extensions by @sviperll
* v2.8.1 - 2015-12-03
    * Extensions by @sviperll
* v2.8.0 - 2015-10-12
    * Requires Java 1.6
    * fixed potential double imports
    * added virtual blocks
    * integrated [sviperll](https://github.com/sviperll)'s [metachicory](https://github.com/sviperll/chicory/tree/master/metachicory)
* v2.7.11 - 2015-09-24
    * Bugfix release
    * removed half done CSE implementation (issue #18)
    * improved handling of directClasses
    * added enumConstantReference
* v2.7.10 - 2015-06-30
    * Synchronized block added
    * initial support for lambda expressions
* v2.7.9 - 2015-03-19
    * Minor extensions for error types
* v2.7.8 - 2015-02-05
    * Enum constants for annotation parameters
* v2.7.7 - 2014-09-17
    * mainly API extensions
* v2.7.6 - 2014-09-02
    * Extended annotation parameter handling API
* v2.7.5 - 2014-08-14
    * Support for multiple boundaries added (like `T extends AnyClass & Serializable`)
* v2.7.4 - 2014-06-12
    * Bugfix release
* v2.7.3 - 2014-05-23
    * Bugfix release
* v2.7.2 - 2014-05-21
    * now on Maven Central
* v2.7.1 - 2014-05-19
    * now as OSGi bundle
* v2.7.0 - 2014-05-16
    * API extensions
* v2.6.4 - 2014-04-10
* 2013-09-23: Changes from https://github.com/UnquietCode/JCodeModel have been incorporated.

## Contribution

Pull requests must follow my personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md)

### Tabs vs spaces

This project uses double-space for indentation. If you want to use tabs, you can ask git to modify the files when commiting them and when pulling them. For this, from your project directory : 

 - edit the file .git/info/attributes to make it contain `*.java filter=tabspace` . This will tell git to apply the script tabspace on the *.java files
 - run `git config filter.tabspace.clean 'expand --tabs=2 --initial'` to ask git to replace tabs with two spaces on commit of *.java files.
 - run `git config filter.tabspace.smudge 'unexpand --tabs=2 --first-only'` to request git to replace double spaces with two tabs on checking a *.java file out.


### Eclipse

For eclipse, a formatter xml and a cleanup xml are present in the meta/formatter/eclipse/ directory. You can load them from the "project properties > java code style" settings. Check "Enable project specific settings", then load them.

NOTE : you also need to change the save actions to make them meet the clean up actions. Save actions are done even when they are not present in the clean up.



---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
It is appreciated if you star the GitHub project if you like it.