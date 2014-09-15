VALue Java Objects Generator (VALJOGen)
=======================================

#0. About VALJOGen

The VALJOGen project can be used to generate Java 7/8+ style value classes from annotated Java interfaces.

The project consist of 4 modules:
- Annotations (all source-level, with no dependencies). It's jar file is not normally used by 3rd party projects unless one want to compile code using the annotations without the annotation processor.
- The annotation processor and related tests. It's jar file embeds all annotations along with a few dependencies.
- Integration tests.
- Examples

#1. VALJOGen FEATURES

+ Can generate **mutable or immutable value objects** with **auto-implemented getters and setters from interfaces**.
+ Support for auto-implementing factory methods, constructors, Object.**hashCode**, Object.**equals**, Object.**toString*, Comparable.**compareTo**, **Serializable** etc.
+ **Extremely customizable** code output. You can change every aspect for the generated class and even add your own code using [StringTemplate 4](http://www.stringtemplate.org) based custom templates.
+ Great support for **immutable objects**, including final fields and immutable setters that return new instances.
+ Support for guards (checks) against null arguments, synchronized/unsynchronized mutable objects, custom getter/setter prefixes, injection of base class and extra interfaces into implementation etc.
+ The annotation processor **run on any standard Java developer tool** running **JDK1.8+** incl. Does not require plugins to **work nicely with IDE's** (as long as they offer the usual standard annotation processor support).
+ Outputs concise, ***modern and nicely formated Java 7+ source code** as if written by hand.
+ Generated code has **no runtime dependencies** on VALJOGen!
+ Sensible defaults and auto-detection makes tool work out-of-the-box with very little work.

*Compared to other tools like for example [projectlombok] (http://projectlombok.org/) VALJOGen is much **more customizable, more powerfull, less intrusive and offer less integration problems**. VALJOGen does not
move/mess around with your code, does not introduce runtime dependencies into your code and just works with any standard Java tools including IDE's. Finally, unlike projectlombok, it use standard API's (no hacks)
so there is much less risk of problems when a new JDK or IDE is released.*

#2. HOW TO USE VALJOGen IN YOUR PROJECTS:

The VALJOGen annotationprocessor is standard since Java 6 and may be used with any standard-complient JDK1.8+ based tool like javac, eclipse, maven etc. Refer to the [getting started document](GETSTARTED.md) for more information.

#3. HOW TO EXTEND VALJOGen AND CONTRIBUTE YOUR CHANGES:

Contributors and contributions are welcome. [Refer to the contribution guide](CONTRIBUTING.md).

#4. KNOWN ISSUES:
- Maven does not always detect correctly when to rebuild after changes in templates. Do a `"mvn clean"` if this happens.
- Generated code only get the correct method parameter names if you add the "-parameters" option to javac.
- No support (yet) for generic wildcards and overloaded custom methods.
- No support for non-default base class constructors.
- Support for XML/JSON serialization should be easier.

#5. RELEVANT LITTERATURE ABOUT JAVA VALUE OBJECTS:
- http://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html
- http://blog.joda.org/2014/03/valjos-value-java-objects.html
- https://blogs.oracle.com/jrose/entry/value_types_in_the_vm
- http://martinfowler.com/bliki/ValueObject.html
- http://c2.com/cgi/wiki?ValueObject

#6. LICENSE
- See [LICENSE file](./LICENSE).

#7. SUPPORT
- Free [Google group discussions] (http://groups.google.com/group/valjogen)
- Paid email support : valjogen (AT) 41concepts |dot| com

/ Morten M. Christensen, 41concepts (http://www.41concepts.com)