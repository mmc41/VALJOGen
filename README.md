VALue Java Objects Generator (VALJOGen)
=======================================

#0. About VALJOGen

The VALJOGen project can be used to generate Java 8+ style value classes from annotated Java interfaces.

The project consist of 3 modules:
- Annotations (all source-level, with no dependencies). It's jar file is not normally used by 3rd party projects unless one want to compile code using the annotations without the annotation processor.
- The annotation processor and related tests. It's jar file embeds all annotations along with a few dependencies.
- Integration tests.

#1. HOW TO USE VALJOGen IN YOUR PROJECTS:

VALJOGen annotations are source-level and needed by compiler only. There are no-runtime dependencies and no libraries that you need to add to your
classpath at runtime (unless you explicitly add references yourself to 3rd party code). You do need to add the VALJOGen annotationprocessor jar
to your compile path though.

VALJOGen uses standard Java annotation processors (`JSR 269`) and should work any Java tool running `JDK 1.8+`. Below are listed some ways of using VALJOGen with porpular
tools (do replace *XXX* with latest version)

Here is a simple example of how to use a VALJOGen annotation to generate source code for a Java Value Class called MySimpleImpl:

```java
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate("MySimpleImpl")
public interface SimpleInterface
{
    public Object getObject();
    public String getString();
}
```

##Adding VALJOGen annotationprocessor to JavaC compiler:

```Bash
javac -cp valjogen-annotationprocessor-*XXX*.jar -s DestinationDirForGeneratedSources -d DestinationDirForOutputClasses SourceDirForYourCodeUsingTheAnnotationProcessor.java
```

##Adding VALJOGen annotationprocessor to Maven:

Use `Maven 3.2.0` or later and add the dependency (*NOTE: THIS IS FOR THE FUTURE - PROJECT ARTIFACTS ARE NOT IN MAVEN CENTRAL YET*):

```Xml
<dependency>
    <groupId>com.fortyoneconcepts.valjogen.annotationprocessor</groupId>
    <artifactId>valjogen-annotationprocessor</artifactId>
    <version>*XXX*</version>
</dependency>
```

##Adding VALJOGen annotationprocessor to Eclipse:

```
Open Window > Preferences > Maven > Annotation processing or right-click on your project > Properties > Maven > Annotation processing to select the Annotation Processing strategy of your choice.
```

##Adding VALJOGen annotationprocessor to Eclipse using Maven *(CURRENTLY UNTESTED)*:

1. Install Eclipse Luna 4.4+
2. Install Eclipse plugin m2e (from eclipses build-in "Luna" update site)
3. Install jbosstools's m2e-apt plugin from (from update site "http://download.jboss.org/jbosstools/updates/m2e-extensions/m2e-apt")

#3. HOW TO EXTEND/MODIFY/MAINTAIN VALJOGen:

See readme in annotaton processor project for implementation details.

In addition, to update readme pages in GitHub Flavored Markdown (`.md`) format use these 2 eclipse plugins:

- GitHub Flavored Markdown viewer: https://raw.github.com/satyagraha/gfm_viewer/master/p2-composite/
- Markdown editor: http://www.winterwell.com/software/updatesite/


#4. KNOWN ISSUES:
- Maven does not always detect correctly when to rebuild after changes in templates. Do a `"mvn clean"` if this happens.
- Generated code only get the correct method parameter names if you add the "-parameters" option to javac.
- Some configuration options are not yet finished. See javadocs for details.

#5. RELEVANT LITTERATURE ABOUT JAVA VALUE OBJECTS:
- http://docs.oracle.com/javase/8/docs/api/java/lang/doc-files/ValueBased.html
- http://blog.joda.org/2014/03/valjos-value-java-objects.html
- https://blogs.oracle.com/jrose/entry/value_types_in_the_vm
- http://martinfowler.com/bliki/ValueObject.html
- http://c2.com/cgi/wiki?ValueObject

#6. LICENSE
- See [LICENSE file](./LICENSE).

#Author and contact info
**Morten M. Christensen, [valjogen (AT) 41concepts |dot| com](http://www.41concepts.com)**