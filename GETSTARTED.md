HOW TO GET STATED USING VALJOGen IN YOUR PROJECTS:
==================================================

#0. Using VALJOGen

VALJOGen annotations are source-level and needed by compiler only. There are no-runtime dependencies and no libraries that you need to add to your
classpath at runtime (unless you explicitly add references yourself to 3rd party code). You do need to add the VALJOGen annotationprocessor jar
to your compile path though.

VALJOGen uses standard Java annotation processors (`JSR 269`) and should work any Java tool running `JDK 1.8+`. Below are listed some ways of using VALJOGen with porpular
tools (do replace *XXX* with latest version)

#1. Examples

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

Refer to the [examples project](valjogen-examples/readme.md) for a full list of examples.


#1. Using VALJOGen with JavaC compiler:

```Bash
javac -cp valjogen-annotationprocessor-*XXX*.jar -Acom.fortyoneconcepts.valjogen.SOURCEPATH=SourceDirForYourCode -s DestinationDirForGeneratedSources -d DestinationDirForOutputClasses SourceDirForYourCodeUsingTheAnnotationProcessor.java
```

#2. Using VALJOGen with Maven:

Use `Maven 3.2.0` or later and add the dependency (*NOTE: THIS IS FOR THE FUTURE - PROJECT ARTIFACTS ARE NOT IN MAVEN CENTRAL YET*):

```Xml
<dependency>
    <groupId>com.fortyoneconcepts.valjogen.annotationprocessor</groupId>
    <artifactId>valjogen-annotationprocessor</artifactId>
    <version>*XXX*</version>
</dependency>
```

#3. Using VALJOGen with Eclipse:

```
In Eclipse open project Properties/Java Compiler/Annoation Processing and enable Annotation processing. Then add a key "com.fortyoneconcepts.valjogen.SOURCEPATH" pointing to the source directory for your project.
```

## Using VALJOGen with Eclipse and Maven *(CURRENTLY UNTESTED)*:

1. Install Eclipse Luna 4.4+
2. Install Eclipse plugin m2e (from eclipses build-in "Luna" update site)
3. Install jbosstools's m2e-apt plugin from (from update site "http://download.jboss.org/jbosstools/updates/m2e-extensions/m2e-apt")

#4. HOW TO EXTEND/MODIFY/MAINTAIN VALJOGen:

See [readme in annotaton processor project](valjogen-processor/readme.md) for implementation details.

In addition these two 2 eclipse plugins may be useful to update readme pages in GitHub Flavored Markdown (`.md`):

- GitHub Flavored Markdown viewer: https://raw.github.com/satyagraha/gfm_viewer/master/p2-composite/
- Markdown editor: http://www.winterwell.com/software/updatesite/

#5. SUPPORT
- Free [Google group discussions] (http://groups.google.com/group/valjogen)
- Paid email support : valjogen (AT) 41concepts |dot| com

/ Morten M. Christensen, 41concepts (http://www.41concepts.com)