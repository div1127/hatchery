## hatchery

An IDE for ROS development. Language support and code assistance for ROS files. 

### Running

First, install Java (JDK 8 or higher). Then you will need to run:

`./gradlew runIde`

To open an existing ROS project inside the IDE, you can provide an absolute path to a ROS project like so:

`./gradlew runIde -Project="<absolute path to ROS project>"`

### Features 

Currently, `hatchery` supports the following types of files:

* .launch
* package.xml
* .py
* .yaml
* CMake
* Bash

It also supports refactoring and navigation from references in:

* roslaunch
* rospackage

### Planning

- [x] Implement preliminary project structure, XML. How is code compiled and run?
- [x] Write an MVP/POC app that supports file renaming and refactoring.
- [ ] Add support for project templates and skeleton project creation.
- [x] Add support for deploying a project from the local machine to the remote.
- [ ] Add support for monitoring and tracking running code, viewing logs.
    - [ ] Save to local disk
    - [ ] Searching the log
- [ ] Collect crash dumps and link to the corresponding code points.
    - [ ] Link stack traces to source code
    - [ ] Copy environment info and crash dump to clipboard

### Overview

* **Syntax support** - Highlighting, navigation, autocompletion
* **Program analysis** - Code inspections, intentions, and linting
* **Testing support** - Unit and integration testing, code coverage
* **Project creation** - Project setup and boilerplate code generation
* **Dependency management** - Track installed and missing packages
* **Monitoring utils** - Logging, diagnostics, profiling and visualization
* **Crash analytics** - Enhanced stack traces with source navigation
* **Build automation** - Delta rebuilds, cmake magic, code hotswap
* **ROS integration** - Nodes, topics, services, parameters, graphs
* **Duckumentation** - Usage instructions and supported features