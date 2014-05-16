jcodemodel
==========

A fork of the com.sun.codemodel 2.7-SNAPSHOT.
The classes in this project use a different package name `com.helger.jcodemodel` to avoid conflicts 
with other `com.sun.codemodel` instances that might be floating around in the classpath.  

News and noteworthy:

2014-04-10: Release 2.6.4
2013-09-23: Changes from https://github.com/UnquietCode/JCodeModel have been incorporated.

#Maven usage
Add the following to your pom.xml to use this artifact:
```
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
  <version>2.6.4</version>
</dependency>
```
