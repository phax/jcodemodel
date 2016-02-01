#jcodemodel

[![Build Status](https://travis-ci.org/phax/jcodemodel.svg?branch=master)](https://travis-ci.org/phax/jcodemodel)

﻿[![Gitter chat](https://badges.gitter.im/phax/jcodemodel.svg)](https://gitter.im/phax/jcodemodel)

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.
That of course implies, that this artefact cannot directly be used with JAXB, since the configuration of 
this would be very tricky.

A site with the links to the [API docs](http://phax.github.io/jcodemodel/) etc. is available.

News and noteworthy:

* 2016-xx-yy: Release 2.8.3 - Add support for single line comments in blocks
* 2016-01-19: Release 2.8.2 - Customizable new line string and character set; extensions by @sviperll
* 2015-12-03: Release 2.8.1 - Extensions by @sviperll
* 2015-10-12: Release 2.8.0 - Requires Java 1.6; fixed potential double imports; added virtual blocks; integrated [sviperll](https://github.com/sviperll)'s [metachicory](https://github.com/sviperll/chicory/tree/master/metachicory)
* 2015-09-24: Release 2.7.11 - Bugfix release; removed half done CSE implementation (issue #18); improved handling of directClasses; added enumConstantReference
* 2015-06-30: Release 2.7.10 - Synchronized block added; initial support for lambda expressions
* 2015-03-19: Release 2.7.9 - Minor extensions for error types
* 2015-02-05: Release 2.7.8 - Enum constants for annotation parameters
* 2014-09-17: Release 2.7.7 - mainly API extensions
* 2014-09-02: Release 2.7.6 - Extended annotation parameter handling API
* 2014-08-14: Release 2.7.5 - Support for multiple boundaries added (like `T extends AnyClass & Serializable`)
* 2014-06-12: Release 2.7.4 - Bugfix release
* 2014-05-23: Release 2.7.3 - Bugfix release
* 2014-05-21: Release 2.7.2 - now on Maven Central
* 2014-05-19: Release 2.7.1 - now as OSGi bundle
* 2014-05-16: Release 2.7.0 - API extensions
* 2014-04-10: Release 2.6.4
* 2013-09-23: Changes from https://github.com/UnquietCode/JCodeModel have been incorporated.

#Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>jcodemodel</artifactId>
  <version>2.8.2</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
