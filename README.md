<a name="jumbotron-start"/>

# About VALJOGen

VALJOGen is a **VALue Java Objects Generator** that can be used to generate modern Java 7/8+ value classes from annotated Java interfaces.

**FEATURES:**

+ Can generate **value objects** with **auto-implemented getters and setters from your interfaces**.
+ Support for auto-implementing factory methods, constructors, Object.**hashCode**, Object.**equals**, Object.**toString**, Comparable.**compareTo**, **Serializable** etc.
+ **Extremely customizable** code output. You can change every aspect for the generated class and even add your own code using [StringTemplate 4](http://www.stringtemplate.org) based custom templates.
+ Great support for **immutable value objects**, including final fields and immutable *setters* that return new instances.
+ Support for synchronized/unsynchronized **mutable value objects**.
+ Full support for overriding **custom base classes** with both default and non-default base constructors, for overriding base class methods, implementing abstract properties etc.
+ Support for guards (checks) against null arguments, custom getter/setter prefixes, injection of extra interfaces into implementation etc.
+ The annotation processor **runs on any standard Java developer tool** running ** JDK1.8+**. **Works nicely with IDE's** without plugins (as long as they offer the usual standard annotation processor support).
+ Outputs **modern and nicely formatted Java 1.7+ source code** as if written by hand - without reflection and without any overhead.
+ Generated code has **no runtime dependencies** on VALJOGen!
+ Sensible defaults and auto-detection makes tool work out-of-the-box with very little work.

=> For a quick introduction see the [getting started document](GETSTARTED.md) or look at some of the [examples](http://valjogen.41concepts.com/examples.html).

<sub>Why VALJOGen?: *Compared to other tools like for example [projectlombok](http://projectlombok.org/) or [Joda Beans](http://www.joda.org/joda-beans/) VALJOGen is much more customizable, more powerfull, less intrusive, offer less integration problems and generated code has no performance overhead (no use of reflection). VALJOGen does not move/mess around with your code or hide the generated code from you and your debugger. VALJOGen does not require you to write your code in a new way (or subclass anything other then your own plain Java interfaces), does not introduce new runtime dependencies into your codebase and just works with any standard Java tools including IDE's. Finally, unlike some alternatives, it use standard API's (no hacks or virtual "language extensions") so there is much less risk of problems when a new JDK or IDE is released.*</sub>

<a name="jumbotron-end"/>

## 1. MODULES

The VALJOGen project consist of the following modules:

- Annotations (all source-level, with no dependencies). It's jar file is not normally used by 3rd party projects unless one want to compile code using the annotations without the annotation processor. For details refer to the [JavaDoc Api](http://valjogen.41concepts.com/apidocs/com/fortyoneconcepts/valjogen/annotations/package-summary.html).
- The annotation processor and related tests. It's jar file embeds all annotations along with a few dependencies.
- Integration tests.
- [Examples](http://valjogen.41concepts.com/examples.html). Where to look for detailed examples of how to use some of the more powerful features of the annotation processor.

## 2. HOW TO USE VALJOGen IN YOUR PROJECTS:

The VALJOGen annotationprocessor is standard since Java 6 and may be used with any standard-complient JDK1.8+ based tool like javac, eclipse, maven etc. Refer to the [getting started document](GETSTARTED.md) for more information.

## 3. HOW TO EXTEND VALJOGen AND CONTRIBUTE YOUR CHANGES:

Contributors and contributions are welcome. [Refer to the contribution guide](CONTRIBUTING.md).

## 4. KNOWN ISSUES:
- Maven does not always detect correctly when to rebuild after changes in templates. Do a `"mvn clean"` if this happens.
- Generated code only get the correct method parameter names if you add the "-parameters" option to javac.
- No support for non-default base class constructors.
- Support for XML/JSON serialization should be easier.

## 5. RELEVANT LITTERATURE ABOUT JAVA VALUE OBJECTS:
- <http://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html>
- <http://blog.joda.org/2014/03/valjos-value-java-objects.html>
- <https://blogs.oracle.com/jrose/entry/value_types_in_the_vm>
- <http://martinfowler.com/bliki/ValueObject.html>
- <http://c2.com/cgi/wiki?ValueObject>

## 6. LICENSE
- See [LICENSE file](LICENSE.md).

## 7. SUPPORT
- [Main website](http://valjogen.41concepts.com)
- Free [Google group discussions](http://groups.google.com/group/valjogen)
- Paid email support : <valjogen(at)41concepts(dot)com>

## 8. CREDITS

VALJOGen is build on [JDK 1.8+](http://www.oracle.com/technetwork/java/index.html) and has benefitted from using the following excellent open-source libraries and tools:

- [Maven](http://maven.apache.org/) with various plugins.
- [Eclipse](https://www.eclipse.org/)
- [StringTemplate 4](http://www.stringtemplate.org/)
- [EqualsVerifier](http://www.jqno.nl/equalsverifier/)
- [com.google.testing.compile](https://github.com/google/compile-testing)

The website uses the following great open-source libraries and tools:

- [Ruby](https://www.ruby-lang.org/en/)
- [nanoc](http://nanoc.ws/)
- [kramdown](http://kramdown.gettalong.org/) with [coderay](http://coderay.rubychan.de/)
- [Eclipse GitHub Flavored Markdown viewer](https://raw.github.com/satyagraha/gfm_viewer/master/p2-composite/)
- [Eclipse Markdown editor](http://www.winterwell.com/software/updatesite/)

VALJOGen development is supported by [41concepts](http://www.41concepts.com) a Danish software R&D and consulting company.

/ Morten M. Christensen, [41concepts](http://www.41concepts.com)