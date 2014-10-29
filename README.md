<a name="jumbotron-start"/>

# About VALJOGen

<abbr title="Value Java Object Generator">VALJOGen</abbr> can be used to generate modern Java 7/8+ value classes from annotated Java interfaces.

**FEATURES:**

+ Can generate **value objects** with **auto-implemented getters and setters from your interfaces**.
+ Support for auto-implementing factory methods, constructors, Object.**hashCode**, Object.**equals**, Object.**toString**, Comparable.**compareTo**, **Serializable** etc.
+ **Extremely customizable** code output. You can change every aspect for the generated class and even add your own code using [StringTemplate 4](http://www.stringtemplate.org) based custom templates.
+ Great support for **immutable value objects**, including final fields and immutable *setters* that return new instances.
+ Support for synchronized/unsynchronized **mutable value objects**.
+ Full support for overriding **custom base classes** with both default and non-default base constructors, for overriding base class methods, implementing abstract properties etc.
+ Support for guards (checks) against null arguments, custom getter/setter prefixes, injection of extra interfaces into implementation etc.
+ The annotation processor **runs on any standard Java developer tool** running **JDK1.8+**. **Should work nicely with IDE's** without plugins (as long as they offer the usual standard annotation processor support).
+ Use standard Java API's (no hacks or virtual "language extensions") so less risk of problems when a new JDK or IDE is released compared to some alternatives.
+ Outputs **modern and nicely formatted Java 1.7+ source code** as if written by hand - without reflection and without any overhead.
+ Generated code has **no runtime dependencies** on VALJOGen!
+ Sensible defaults and auto-detection makes tool work out-of-the-box with very little work.

<blockquote>One of the strong benefits of VALJOGen is extreme customization facilities. With all other tools I have run into limitations like: Can not add my own custom methods, can not subclass or add a base class constructor with X arguments, can not make class abstract or change the way hash codes are calculated etc.
<br/><br/>With VALJOGen everything is possible. Many options can be configured easily by annotations and if that is not enough you can supply a custom string template where you can extend or modify EVERYTHING you want in the generated output.</blockquote>

<a name="important-start"/>

=> For a quick introduction see the [getting started document](GETSTARTED.md) or look at some of the [examples](http://valjogen.41concepts.com/examples.html).

<a name="important-end"/>

<a name="jumbotron-end"/>

## 1. MODULES

The VALJOGen project consist of the following modules:

- Annotations (all source-level, with no dependencies). It's jar file is not normally used by 3rd party projects unless one want to compile code using the annotations without the annotation processor. For details refer to the [JavaDoc Api](http://valjogen.41concepts.com/apidocs/com/fortyoneconcepts/valjogen/annotations/package-summary.html).
- The annotation processor and related tests. It's jar file embeds all annotations along with a few dependencies.
- Integration tests.
- [Examples](http://valjogen.41concepts.com/examples.html). Where to look for detailed examples of how to use some of the more powerful features of the annotation processor.

## 2. HOW TO USE VALJOGen IN YOUR PROJECTS:

The VALJOGen annotationprocessor may be used with any standard-complient JDK1.8+ based tool like javac, eclipse, maven etc. Refer to the [getting started document](GETSTARTED.md) for more information.

## 3. HOW TO EXTEND VALJOGen AND CONTRIBUTE YOUR CHANGES:

Contributors and contributions are welcome. [Refer to the contribution guide](CONTRIBUTING.md).

## 4. KNOWN ISSUES:
- Sometimes javac will fail with "java.lang.IllegalStateException: endPosTable already set" - in particular if the output directory is not empty. I suspect this is an error in the JDK but I do not know. If it happens clean(!) to remove existing ouputs, rebuild and it should succed.
- Maven does not always detect correctly when to rebuild after changes in templates. Do a `"mvn clean"` if this happens.
- Generated code only get the correct method parameter names if you add the "-parameters" option to javac.
- Support for XML/JSON serialization should be easier.
- Generator does not account for bounded generic type arguments when deciding if something is serializable or comparable.
- Due to [Eclipse bug 382590][eclipsebug] VALJOGen can not generated correct code when subclassing a generic interface. Please [vote for the bug in bugzilla][eclipsebug] to help get it fixed.

[eclipsebug]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=382590  "Eclipse bug 382590"

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
- [bootstrap](http://getbootstrap.com)
- [kramdown](http://kramdown.gettalong.org/) with [coderay](http://coderay.rubychan.de/)
- [Eclipse GitHub Flavored Markdown viewer](https://raw.github.com/satyagraha/gfm_viewer/master/p2-composite/)
- [Eclipse Markdown editor](http://www.winterwell.com/software/updatesite/)

VALJOGen development is supported by [41concepts](http://www.41concepts.com) a Danish software R&D and consulting company.

/ [Morten M. Christensen](http://www.linkedin.com/in/mortench), [41concepts](http://www.41concepts.com)