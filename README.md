jcodemodel
==========

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.  

News and noteworthy:

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
<!-- Required only for versions before 2.7.2 -->
<repositories>
  <repository>
    <id>phloc.com</id>
    <url>http://repo.phloc.com/maven2</url>
    <releases>
      <enabled>true</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>

...

<dependency>
  <groupId>com.helger</groupId>
  <artifactId>jcodemodel</artifactId>
  <version>2.7.4</version>
</dependency>
```
