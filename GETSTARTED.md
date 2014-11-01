<a name="jumbotron-start"/>

# Get started using VALJOGen

<abbr title="Value Java Object Generator">VALJOGen</abbr> annotations are source-level and needed by compiler only. There are no-runtime dependencies and no libraries that you need to add to your classpath at runtime (unless you explicitly add references yourself to 3rd party code). You do need to add the VALJOGen annotationprocessor jar to your compile path though. Files are available at maven central or can be downloaded manually as noted [here](DOWNLOADS.md)

VALJOGen uses standard Java annotation processors (`JSR 269`) and should work any Java tool running `JDK 1.8+` with a target of `JDK 1.7` or later. Below are listed some ways of using VALJOGen with popular tools.

**Simple example:**

Here is a simple example of how to use a VALJOGen annotation to generate source code for a Java Value Class called SimpleInterfaceImpl:

```java
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to control the name of the generated implementation class.
*/
@VALJOGenerate(name="SimpleInterfaceImpl")
public interface SimpleInterfaceWithNamedOutput
{
    public Object getObject();
    public String getString();
}
```

Refer to the [collection of examples along with their generated output](http://valjogen.41concepts.com/examples.html) for output and the full list of examples.

<a name="important-start"/>

**Advice:**

- It is generally a good idea if you structure your project in multiple modules with your interfaces in a module by itself. You can then use the VALJOGen annotation processor to produce output in the first stage only of a multistage build. Thus you will not have to re-generate the implementation objects unless needed. This saves time and makes it easier to work with generated code.
- Do **not** change the code of the generated classes manually. Instead update the interface, customize and rebuild using the annotation processor to regenerated output with changes.
- Do **not** (normally) check-in generated code in your source repository. If you use git you can configure the .gitignore file for this.

<br/>*In addtion, make sure output directory is empty when running the annotation processor. Some javac versions are fragile and might give an exception <code>java.lang.IllegalStateException: endPosTable already set</code> if you forgot to clean output directories before running the processor.*

<a name="important-end"/>

<a name="jumbotron-end"/>

## 1. Using VALJOGen processor with Maven:

Use `Maven 3.2.0` or later and add the dependency which will add the annotation processor and it's included annotations to your classpath:

```Xml
<dependency>
  <groupId>com.41concepts</groupId>
  <artifactId>valjogen-annotationprocessor</artifactId>
  <version>1.0.1</version>
  <optional>true</optional>
</dependency>
```

If you are compiling without the annotation processor or using the <code>-processorpath</code> option you may want to add just the annotations:

```Xml
<dependency>
  <groupId>com.41concepts</groupId>
  <artifactId>valjogen-annotations</artifactId>
  <version>1.0.1</version>
  <optional>true</optional>
</dependency>
```

In both cases the dependency is compile-time only. Unfortunately, there is no good way to specify this with maven but you might get succes marking the dependency as optional as stated above.

In addition you should configure the following for the maven-compiler-plugin:

```Xml
<plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-compiler-plugin</artifactId>
   ...
   <configuration>
     <compilerArgs>
       <arg>-parameters</arg>   <!-- Ensure parameter meta data is available for best code generation -->
       <arg>-Acom.fortyoneconcepts.valjogen.SOURCEPATH=${basedir}/src/main/java</arg> <!-- Specify where to locate sources -->
     </compilerArgs>
   </configuration>
</plugin>
```

See also [here](https://github.com/41concepts/VALJOGen/blob/master/valjogen-examples/standalone.xml) for a complete example of a maven pom file.

If you are debugging custom templates you may also want to add extra compilerArgs argument like this:

```Xml
       <arg>-Acom.fortyoneconcepts.valjogen.logLevel=INFO</arg>
       <arg>-Acom.fortyoneconcepts.valjogen.LOGFILE=${basedir}/target/valjogen.log</arg>
```

Inside the generated logfile you will then find useful (but a bit complex) dumps of the configuration and models etc.

## 2. Using VALJOGen with JavaC compiler:

```Bash
javac -parameters -cp valjogen-annotationprocessor-1.0.1.jar -Acom.fortyoneconcepts.valjogen.SOURCEPATH=SourceDirForYourCode -s DestinationDirForGeneratedSources -d DestinationDirForOutputClasses SourceDirForYourCodeUsingTheAnnotationProcessor.java
```

The example above makes the annotation processor available on the normal class path, uses the JDK1.8+ <code>-parameter</code> option to enable parameter names processing by the annotation processor and the <code>-Akey[=value]</code> option to let the processor know the source path.

Alternatively, it is possible to compile using the -processorpath option. In this case the classes in the annotation processor is not seen on the classoath so do add the jar file with the VALJOGen annotations on the classpath seperately as shown below.

```Bash
javac -parameters -cp valjogen-annotations/target/valjogen-annotations-1.0.1.jar -processorpath ../valjogen-processor/target/valjogen-annotationprocessor-1.0.1.jar -Acom.fortyoneconcepts.valjogen.SOURCEPATH=SourceDirForYourCode -s DestinationDirForGeneratedSources -d DestinationDirForOutputClasses SourceDirForYourCodeUsingTheAnnotationProcessor.java
```

## 3. Using VALJOGen with Eclipse:

Due to [Eclipse bug 382590][eclipsebug] VALJOGen can not generated correct code when subclassing a generic interface. Apart from this use case the annotation processor works inside Eclipse if you do the following:

1. In Eclipse first add valjogen-annotations-1.0.1.jar to the class path.
2. Open project Properties/Java Compiler/Annoation Processing, enable Annotation processing and add valjogen-annotationprocessor-1.0.1.jar as processor.
3. Add a key "com.fortyoneconcepts.valjogen.SOURCEPATH" pointing to the source directories for your project.

*Bug 382590 in Eclipse was reported in 2012 and has not been fixed yet. If you want VALJOGen and other annotation processors to work perfectly in eclipse then [please cast your vote for the bug at Eclipse's bugzilla][eclipsebug].*

[eclipsebug]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=382590  "Eclipse bug 382590"

### Using VALJOGen with Eclipse and Maven *(CURRENTLY UNTESTED)*:

1. Install Eclipse Luna 4.4+
2. Install Eclipse plugin m2e (from eclipses build-in "Luna" update site)
3. Install jbosstools's m2e-apt plugin from (from update site "http://download.jboss.org/jbosstools/updates/m2e-extensions/m2e-apt")

## 4. Using VALJOGen with ANT *(CURRENTLY UNTESTED)*:

```Xml
<javac srcdir="${basedir}/src/main/java"
       destdir="build/classes"
       classpath="valjogen-annotationprocessor-1.0.1.jar">
       <compilerarg value="-parameters" />
       <compilerarg value="-Acom.fortyoneconcepts.valjogen.SOURCEPATH=${basedir}/src/main/java"/>
</javac>
```

Where basedir is a defined property pointing to the absolute file path of the project.

## 5. Customizing generated value classes

VALJOGen output can be customized in a number of ways as listed below (in a order of preference):

1. Use existing options in VALJOGenerate and VALJOConfigure annotations. Alternatively, specify same options in qualified form as -A options to the annotation processor or in a "valjogen.properties" file located at the root of your classpath.
2. Customization by inheritance A: Specify a BaseClass with your custom code in your VALJOConfigure annotation.
3. Customization by inheritance B: Subclass the generates class(es) and put your custom stuff there (use VALJOConfigure modifier option to make generated class abstract or non final)
4. Add custom method(s) in a custom template group file specified in your VALJOConfigure annotation. Do this if you need to create dynamic code depending on how the class looks like (exact members, properties, methods etc).
5. Override the regions defined in the build-in templates. Also using a custom template group file specified in your VALJOConfigure annotation. Do this if you want to make small changes to existing functionality or if you want to insert something special like an innner class etc.
6. Override existing template methods. Also using a custom template group file specified in your VALJOConfigure annotation. Do this if you want to completely change an existing generated method and it's regions are not flexible enough.

See [VALJOConfigure JavaDocs](apidocs/com/fortyoneconcepts/valjogen/annotations/VALJOConfigure.html#customJavaTemplateFileName) and the [examples](http://valjogen.41concepts.com/examples.html) for details about custom templates. See also the [StringTemplate 4 cheat sheet](https://theantlrguy.atlassian.net/wiki/display/ST4/StringTemplate+cheat+sheet) for syntax and general tips.

## 6. Support
- [Main website](http://valjogen.41concepts.com)
- Free [Google group discussions](http://groups.google.com/group/valjogen)
- Paid email support : valjogen (AT) 41concepts (dot) com

## 7. About VALJOGen project internals

See [readme in annotaton processor project](valjogen-processor/README.md) for some implementation details or look at the source.

The [VALJOGen website](http://valjogen.41concepts.com) is generated using [nanoc](http://nanoc.ws/) using [github flavored markdown](https://help.github.com/articles/github-flavored-markdown/) files. See site subproject folder for details.

/ Morten M. Christensen, [41concepts](http://www.41concepts.com)