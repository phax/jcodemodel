# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

`jcodemodel` is a fork of `com.sun.codemodel` 2.7-SNAPSHOT, repackaged under `com.helger.jcodemodel` to avoid classpath conflicts. It is a Java library for programmatically generating Java source code (classes, methods, expressions, statements, annotations, etc.). Java 17 is required since v4.0.0.

## Repository layout (Maven multi-module)

The root `pom.xml` declares three modules:

- `jcodemodel/` — the core code generation library (`com.helger.jcodemodel:jcodemodel`). Licensed CDDL+GPL 1.1 (inherited from the upstream Sun fork).
- `plugin/` — Apache 2 licensed. Contains:
  - `plugin/plugin/` — the `jcodemodel-maven-plugin` that generates Java code at build time.
  - `plugin/generators/` — pluggable generators consumed by the Maven plugin: `csv`, `json`, `yaml`, plus `helloworld` as a reference impl.
- `examples/plugins/` — example projects that exercise the Maven plugin and its generators.

Note: the core library and the plugin/generators are licensed differently — keep license headers correct when adding files (CDDL+GPL for core, Apache 2 for plugin tree).

## Build commands

```bash
# Full build of all modules
mvn clean install

# Build a single module (with its dependencies)
mvn -pl jcodemodel -am clean install
mvn -pl plugin/plugin -am clean install

# Run all tests in a module
mvn -pl jcodemodel test

# Run a single test class / method
mvn -pl jcodemodel test -Dtest=JCodeModelTest
mvn -pl jcodemodel test -Dtest=JCodeModelTest#testSomething

# Skip tests
mvn clean install -DskipTests
```

The parent POM is `com.helger:parent-pom:3.0.3`, which configures the Java toolchain, license headers, and formatting plugins.

## Core library architecture (`com.helger.jcodemodel`)

The library models a Java program as a tree of immutable-ish builder objects rooted at `JCodeModel`. Understanding the type hierarchy is key — most classes follow a strict naming convention:

- `I*` — interfaces (e.g. `IJExpression`, `IJStatement`, `IJDeclaration`, `IJGenerable`, `IJFormatter`, `IJAnnotatable`).
- `AbstractJ*` — abstract base classes (e.g. `AbstractJType`, `AbstractJClass`, `AbstractJClassContainer`).
- `J*` — concrete model nodes (e.g. `JDefinedClass`, `JMethod`, `JBlock`, `JInvocation`, `JConditional`, `JTryBlock`, `JLambda`).
- `E*` — enums (e.g. `EClassType`, `EWildcardBoundMode`).

Key entry points and concepts:

- `JCodeModel` — root container. Owns packages, classes, resource directories, and the file system convention. Use `new JCMWriter(cm).build(...)` to emit sources (the older `cm.build(...)` API is deprecated but still works).
- `JDefinedClass` — a generated class/interface/enum/annotation/record. Created via `JPackage#_class`, `JDefinedClass#_class` (inner), etc.
- Expression/statement split: `IJExpression` (anything that produces a value) vs `IJStatement` (anything that can stand alone in a block). `JBlock` composes statements; `JExpr` is the static factory for atomic expressions (literals, `this`, `super`, refs, casts, …).
- Sub-packages:
  - `exceptions/` — all checked exceptions (`JCodeModelException` is the base; subclasses include `JClassAlreadyExistsException`, `JResourceAlreadyExistsException`, `JErrorClassUsedException`, `JInvalidFileNameException`, `JCaseSensitivityChangeException`).
  - `fmt/` — non-Java resource emission (`JTextFile`, `JBinaryFile`, `JPropertyFile`, `JStaticJavaFile`, `JSerializedObject`).
  - `compile/` — in-memory compilation utilities (`MemoryCodeWriter`, `DynamicClassLoader`, `SourceJavaFile`, `CompiledCodeJavaFile`, `ClassLoaderFileManager`) for compiling generated code on the fly.
  - `writer/` (under `fmt`/output) — `JCMWriter` plus `IJFormatter`/`JFormatter` for source serialization.

When changing the model (new statement, new expression kind, new modifier), expect to touch: the new `J*` class itself, an `IJ*` interface if applicable, a factory in `JExpr`/`JBlock`/parent container, and `JFormatter` so it serializes correctly. Then add tests under `jcodemodel/src/test/java`.

## Maven plugin (`plugin/`)

`jcodemodel-maven-plugin` invokes pluggable generators (each is its own Maven module under `plugin/generators/`) to produce Java sources from input files (CSV, JSON, YAML). The `helloworld` generator is the canonical minimal example to copy when adding a new generator. Examples consuming the plugin live under `examples/plugins/`.

## Coding style (project-specific)

The project uses **two-space indentation** with a **space before parentheses and angle brackets**:

```java
public ResponseEntity <String> doIt (final String sName)
{
  if (sName != null)
    return new ResponseEntity <> (sName, HttpStatus.OK);
  ...
}
```

Eclipse formatter/cleanup XMLs live under `jcodemodel/meta/` (referenced from the README as `meta/formatter/eclipse/`). The project tolerates contributors using tabs locally via the `sh/tabspaces` git filter script — do not commit tab-indented files.

Other conventions enforced by `~/.claude/rules/naming.md` (Hungarian notation, `m_`/`s_` scope prefixes, `I`/`E`/`Abstract` type prefixes, `ID` always uppercase, inline string concatenation in `LOGGER` calls, no `@Override` on pure interface implementations, no `serialVersionUID`) apply throughout this codebase — match the style of the file you are editing.

## Versioning and compatibility

- v4.x requires Java 17.
- v3.x required Java 8; v2.8+ required Java 6.
- v4.2.0 (current `-SNAPSHOT`) removed OSGi bundling, added support for Java `record` types and `TYPE_USE` annotation targets.
- The default output charset is UTF-8 (since v3.2.0).
