VALue Java Objects Generator (VALJOGen) - Annotation Processor
==============================================================

#About this module
This is the main project module that contains the annotation processor code, unit-tests and stubbed integration tests. More non-stubbed integration tests
are available in the seperate integration test project.

#Minimum setup for development
- Install JDK 1.8+
- Install Maven 3.2.0+

Then execute a `"mvn package"` on command line to compile project and run tests.

#Optional setup for eclipse
1. Install Eclipse Luna 4.4+
2. Install Eclipse plugin m2e (from eclipses build-in "Luna" update site)
3. Select Import - Maven - Existing Maven Projects and select root folder of all projects

#Important components are
- The processor package the annotation processor itself.

- The model package with contains a intermediate representation that is inspected when generating output.
  - Top-level model is Clazz which represents a Java class.
  - Configuration of generated output is handled by the Configuration class which bases its configuration on class or package level annotations. These
    can be overruled by externally configured annotation processor key/values. Names of keys, which strictly correspond to annotation method names, are
    defined by ConfigurationOptionKeys.

- A templates folder which contains StringTemplate (v4) template group files. These are responsible for generating the actual output.

#Useful links
- [com.google.testing.compile library](https://github.com/google/compile-testing) - For Testing of annotations processors and access to javax.lang.model.*
- [StringTemplate](http://theantlrguy.atlassian.net/wiki/display/ST4/StringTemplate+4+Documentation) - For maintaining template group files.
- [Debugging Annotation Processors if debuging junit tests is not enough](http://www.pingtimeout.fr/2012/10/debugging-annotation-processor-in-every.html)

/ Morten M. Christensen, 41concepts (http://www.41concepts.com)