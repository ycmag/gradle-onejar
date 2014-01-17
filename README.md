gradle-onejar
=============

Gradle plugin for generating single jar from java/groovy application.

Many thanks to P. Simon Tuffs for [OneJAR library](http://one-jar.sourceforge.net/) 
and [one-jar-ant-task](http://one-jar.sourceforge.net/index.php?page=getting-started&file=ant): 
they are used by gradle-onejar.

##Content of this document

* [Why gradle-onejar?](#why-gradle-onejar)
* [Usage](#usage)
* [Tasks](#tasks)
  * [productBuild](#productbuild-task)
  * [productArchive](#productarchive-task)
  * [copyExplodedResources](#copyexplodedresources-task)
  * [run](#run-task)
  * [debug](#debug-task)
* [Products](#products)
* [Configuration](#configuration)

##Why gradle-onejar?

You wrote a nice java program, now you want to deliver it to the Customers.
Would it not be great to deliver your program as a single jar file, 
maybe with auxiliary .bat or .sh, simplifying program start?

If you decided: "yes, I want it this way", next question will come: how to implement
automatic assembly of the delivery package with your favorite build tool - gradle?

Answer is simple: use gradle-onejar. It's free and it does exactly what you need.

##Usage:

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

##Tasks

gradle-onejar inserts the following tasks into java/groovy application lifecycle:

![task diagram](https://raw.github.com/akhikhl/gradle-onejar/master/doc/task_diagram.png "Gradle-onejar tasks")

the yellow boxes denote standard java/groovy tasks, the blue boxes denote gradle-onejar tasks.

The tasks copyExplodedResources, productBuild and productArchive are internal - you normally never call them directly.

Below is information on each task.

###productBuild task

The task productBuild archives jar-files of the current project and of all it's dependencies in a special "superjar" denoted by path
"${project.projectDir}/build/output/${project.name}.jar".

Additionally this task creates three files: 

1. "${project.name}..bat" file for launching the program under Windows.
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

##Products

gradle-onejar supports concept of multiple products. Each product is essentially 
a configuration with specific dependencies and/or additional files.

If you don't specify any products, gradle-onejar generates the default product for you.

##Configuration

gradle-onejar supports the following configuration:

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

<<<<<<< HEAD
The whole "onejar" configuration is optional. Even if it is omitted, gradle-onejar
will generate the default product with the default properties.
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
to be written to the launcher script (.bat or .sh), so that there parameter
is passed to the program each time the program is started.

**beforeProductGeneration** - optional, closure. When specified, the closure will be invoked
after project is evaluated and before the products are generated. Note that even if there are multiple
products specified, beforeProductGeneration is invoked only once.

**onProductGeneration** - optional closure. When specified, the closure will be invoked
after each product generation. The closure receives two parameters: product hashmap
and outputDir.

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/akhikhl/gradle-onejar/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

