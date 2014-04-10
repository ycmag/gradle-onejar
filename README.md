#gradle-onejar 
[![Maintainer Status](http://stillmaintained.com/akhikhl/gradle-onejar.png)](http://stillmaintained.com/akhikhl/gradle-onejar) 
[![Build Status](https://travis-ci.org/akhikhl/gradle-onejar.png?branch=master)](https://travis-ci.org/akhikhl/gradle-onejar) 
[![Latest Version](http://img.shields.io/badge/latest_version-0.0.8-blue.svg)](https://github.com/akhikhl/gradle-onejar/tree/v0.0.8)
[![License](http://img.shields.io/badge/license-MIT-ff69b4.svg)](#copyright-and-license)

Gradle plugin for generating single jar for JVM-based application.

##Content of this document

* [Why gradle-onejar?](#why-gradle-onejar)
* [Include in build.gradle](#include-in-build.gradle)
* [Simplest use](#simplest-use)
* [Tasks](#tasks)
  * [productBuild](#productbuild-task)
  * [productArchive](#productarchive-task)
  * [copyExplodedResources](#copyexplodedresources-task)
  * [run](#run-task)
  * [debug](#debug-task)
* [Configuration](#configuration)
* [Product-specific configuration](#product-specific-configuration)
* [Copyright and License](#copyright-and-license)

##Why gradle-onejar?

You wrote a nice java program, now you want to deliver it to the Customers.
Would it not be great to deliver your program as a single jar file,
maybe with auxiliary .bat or .sh, simplifying program start?

If you decided: "yes, I want it this way", next question will come: how to implement
automatic assembly of the delivery package with your favorite build tool - gradle?

Answer is simple: use gradle-onejar. It's free and it does exactly what you need.

##Include in build.gradle:

Add the following to "build.gradle" of your web-application:

```groovy
apply from: 'https://raw.github.com/akhikhl/gradle-onejar/master/pluginScripts/gradle-onejar.plugin'
```

then do "gradle build" from the command-line.

Alternatively, you can download the script from https://raw.github.com/akhikhl/gradle-onejar/master/pluginScripts/gradle-onejar.plugin
to the project folder and include it like this:

```groovy
apply from: 'gradle-onejar.plugin'
```

or feel free copying (and modifying) the declarations from this script to your "build.gradle".

##Simplest use

Simply run from the command-line:

```shell
gradle build
```

**Effect:** gradle-onejar first compiles the current project to usual location "build/libs",
then it assembles current project's JAR and all it's dependencies into single JAR
with name:
"build/output/${project.name}-${project.version}/${project.name}.jar".

##Tasks

gradle-onejar inserts the following tasks into java/groovy application lifecycle:

![task diagram](https://raw.github.com/akhikhl/gradle-onejar/master/doc/task_diagram.png "Gradle-onejar tasks")

the yellow boxes denote standard java/groovy tasks, the blue boxes denote gradle-onejar tasks.

The tasks copyExplodedResources, productBuild and productArchive are internal - you normally never call them directly.

Below is information on each task.

###productBuild task

The task productBuild archives jar-files of the current project and of all it's dependencies in a "superjar" denoted by path
"${project.projectDir}/build/output/${project.name}-${project.version}/${project.name}.jar".

Additionally this task creates three files:

1. "${project.name}.bat" file for launching the program under Windows.
2. "${project.name}.sh" file for launching the program under Linux.
3. "VERSION" file containing version information.

###productArchive task

The task productArchive archives the files generated by task productBuild into a single .zip or .tag.gz file.
Exact file extension is chosen depending on the platform. See chapter [configuration](#configuration)
for more information.

###copyExplodedResources task

The task copyExplodedResources copies additional files to the folder "${project.projectDir}/build/output",
so that these files get packed by task productArchive.

###run task

This task starts the application via JavaExec task, passing project classpath and mainclass as task properties.

###debug task

This task starts the application via JavaExec task, passing project classpath and mainclass as task properties.
Additionally it sets property "debug" to "true", so that the programmer can debug the application
under NetBeans, Eclipse or any other IDE, supporting java debugging.

##Configuration

gradle-onejar requires the following configuration:

```groovy
ext {
  mainClass = 'mypackage.MyClass'
}
```

here mypackage.MyClass should denote an existing java class implementing static main function.

gradle-onejar supports (but not requires) the following configuration extension:

```groovy
onejar {
  mainJar ...
  manifest {
    attributes attrName1: attrValue1 [, ...]
  }
  product name: ..., platform: ..., arch: ..., language: ..., suffix: ..., launchers: [ ... ]
  archiveProducts true|false
  additionalProductFiles ... [, ...]
  excludeProductFile ... [, ...]
  launchParameter ...
  beforeProductGeneration Closure
  onProductGeneration Closure
}
```

The whole "onejar" configuration is optional. Even if it is omitted, gradle-onejar
generates the default product with the default properties.
Below is the detailed information on each configuration property.

**mainJar** - optional, java.lang.String or java.io.File or Closure, returning String or File.
When specified, this property defines which jar file contains main function.
When omitted, project.tasks.jar.archivePath is used as main jar.

**manifest** - optional, closure. May contain call to "attributes" function, accepting hashmap
of properties to be written to onejar manifest.
When omitted, gradle-onejar does not alter onejar manifest.

**product** - optional, multiplicity 0..N, hashmap, accepting the following properties:

- **name** - required, string, denotes the name of the product.

- **platform** - optional, string, denotes the target platform. Possible values are "windows" and "linux".
  When omitted, the product is platform-neutral.
  When equals to "windows", default launcher is ".bat" and target archive format is ".zip".
  When equals to "linux", default launcher is ".sh" and target archive format is ".tag.gz".

- **arch** - optional, string, denotes the target architecture. Does not affect product generation (yet).

- **language** - optional, string, denotes the target language. Does not affect product generation (yet).

- **suffix** - optional, string, denotes the suffix to be added to the generated files/folders.

- **launchers** - optional, array, contains one or more strings "windows", "shell".
  When omitted, launchers are defined by the selected platform.
  When equals to "windows", the product is supplied with ".bat" launcher.
  When equals to "shell", the product is supplied with ".sh" launcher.

**archiveProducts** - optional, boolean. When true, gradle-onejar packs the generated product to ".zip" or ".tar.gz" archive -
depending on the specified platform. If platform is not specified, target format is ".tar.gz".

**additionalProductFiles** - optional, multiplicity 0..N, array of File or Closure objects. When specified,
each element is resolved to a file, the latter is added to each product.

**excludeProductFile** - optional, multiplicity 0..N, array of File or Closure objects. When specified,
each element is resolved to a file, the latter is excluded from each product.

**launchParameter** - optional, multiplicity 0..N, string or array of strings
to be written to the launcher script (.bat or .sh), so that these parameters
are passed to the program each time the program is started.

**beforeProductGeneration** - optional, closure. When specified, the closure will be invoked
after project is evaluated and before the products are generated. Note that even if there are multiple
products specified, beforeProductGeneration is invoked only once.

**onProductGeneration** - optional closure. When specified, the closure will be invoked
after each product generation. The closure receives two parameters: product hashmap
and outputDir.

##Product-specific configuration

gradle-onejar supports concept of multiple products. Each product gets it's own "superjar".
For example, if you define two products:

```groovy
onejar {
  product name: 'v2_3'
  product name: 'v2_4'
}
```
then gradle-onejar will generate two products for you:

```shell
onejarMultiConfig-0.0.7-v2_3
  onejarMultiConfig.jar
  onejarMultiConfig.sh
  VERSION
onejarMultiConfig-0.0.7-v2_4
  onejarMultiConfig.jar
  onejarMultiConfig.sh
  VERSION
```

If you don't specify any products, gradle-onejar generates the default product for you.

You can tailor product-specific configurations within your project:

```groovy
configurations {
  // product-specific configurations
  product_v2_3
  product_v2_4
}

dependencies {
  // dependencies common to all products
  compile 'org.apache.commons:commons-lang3:3.0'
  compile 'commons-io:commons-io:2.4'
  // product-specific dependencies
  product_v2_3 'commons-io:commons-io:2.3'
  product_v2_4 'commons-io:commons-io:2.4'
}

onejar {
  // each product automatically recognizes linked configuration and dependencies
  product name: 'v2_3'
  product name: 'v2_4'
}
```

Note that product-specific configurations are linked to gradle-onejar products by convension:
the product with name "XYZ" recognizes and uses gradle configuration "product_XYZ"

##Copyright and License

Copyright 2014 (c) Andrey Hihlovskiy

All versions, present and past, of gretty-plugin are licensed under [MIT license](license.txt).

Many thanks to P. Simon Tuffs for [OneJAR library](http://one-jar.sourceforge.net/)
and [one-jar-ant-task](http://one-jar.sourceforge.net/index.php?page=getting-started&file=ant):
they are used by gradle-onejar.

I do not own "onejar" trademark nor am I affiliated with P. Simon Tuffs in any way.

The One-JAR license is a BSD-style license. Compliance with this license is assured 
by including the "one-jar-license.txt" file in the One-JAR archive. gradle-onejar does this 
automatically, putting this file in a "doc" directory in the archive.
["one-jar-license.txt"](one-jar-license.txt) is also included with the sources of gradle-onejar.
