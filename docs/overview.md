## Project overview

### Maven architecture

The project is split in

 - The [root pom project](../pom.xml)
 - The [core project](../jcodemodel/pom.xml) lib to generate classes and resources programmatically.
 - The [test project](../jccodemodeltests/pom.xml) validates the *generated* classes behavior.
 - The [plugin](../plugin//plugin/pom.xml) to generate java classes in maven
 - The various [plugin generators](../plugin/generators/pom.xml) to load a JCodeModel in the plugin
 - The [examples](../examples/pom.xml) that showcase how to use JCodeModel, the plugin, the generators.
 - The [beta](../beta/pom.xml) contains projects that may be merged into the core, or other modules, but could also be discarded entirely. 

### Core classes

#### JCodeModel

This is the main class. It contains the definition of the classes and resources to generate.

Its main usage is to add classes using the various `_class` methods. Those classes can then be added methods, fields, or other classes.

#### JCMWriter

This allows to export a `JCodeModel`. Typically to a directory.

### Scripts

The scripts present in the [sh directory](../sh) are run using a linux shell. For windows, you rather install git for windows which comes with a `bash` shell.

 - [cleaninstall](../sh/cleaninstall) runs the maven up-to install phases with parrallel execution and no output of the transfer to reduce logs, with a call to the clean phase to delete all intermediate products. It's only useful when you need to ensure other branches don't interfere with the result, like before a release.
 - [install](../sh/install) does it without a clean. That's the main call to typically test changes.
 - [mergeupstream](../sh/mergeupstream) adds an `upstream` repository to you local git, if needed, then checks its `master` commits out in the current branch. This allows to have your local branch up-to-date, for example before submitting a PR.
 - [upgrades](../sh/upgrades) lists the possible dependencies/plugins upgrades. This is purely informative.
 - [voidtest](../sh/voidtest) compiles and run the project, up to the integration-test phase, on a fresh (empty, temporary) maven repository. This will force re-downloading of **all** the libraries and plugins. This is only used when we suspect compiled libraries are generating issues.