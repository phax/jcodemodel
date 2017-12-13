# jcodemodel

[![Build Status](https://travis-ci.org/phax/jcodemodel.svg?branch=master)](https://travis-ci.org/phax/jcodemodel)

﻿[![Gitter chat](https://badges.gitter.im/phax/jcodemodel.svg)](https://gitter.im/phax/jcodemodel)

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.
That of course implies, that this artefact cannot directly be used with JAXB, since the configuration of 
this would be very tricky.

A site with the links to the [API docs](http://phax.github.io/jcodemodel/) etc. is available.

## News and noteworthy

* v3.0.2 - work in progress
  * Fixed method resolution using direct class references (issue #58)
* v3.0.1 - 2017-10-25
  * Added explicit support for invoking `super` - thx to @heruan for pointing this out
  * Added possibility to create a lambda reference from an invocation (issue #56 and PR #57 from @heruan)
* v3.0.0 - 2017-08-06
  * Requires Java 8
  * Reworked #41 so that it is finally working in all cases
  * Add option for classes to not be imported (issue #51)
  * Fixed extra semicolon on Lambdas (issue #53)
* v2.8.6  2016-07-19
  * added PR (issue #49)
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

## Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>jcodemodel</artifactId>
  <version>3.0.1</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
