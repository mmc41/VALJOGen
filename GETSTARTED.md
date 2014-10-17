<a name="jumbotron-start"/>

# Get started using VALJOGen

VALJOGen annotations are source-level and needed by compiler only. There are no-runtime dependencies and no libraries that you need to add to your classpath at runtime (unless you explicitly add references yourself to 3rd party code). You do need to add the VALJOGen annotationprocessor jar to your compile path though. Files are available at maven central or can be downloaded manually as noted [here](DOWNLOADS.md)

VALJOGen uses standard Java annotation processors (`JSR 269`) and should work any Java tool running `JDK 1.8+`. Below are listed some ways of using VALJOGen with popular tools (do replace *XXX* with latest version)

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

**Advice:**

- It is generally a good idea if you structure your project in multiple modules with your interfaces in a module by itself. You can then use the VALJOGen annotation processor to produce output in the first stage only of a multistage build. Thus you will not have to re-generate the implementation objects unless needed. This saves time and makes it easier to work with generated code.
- Do **not** change the code of the generated classes manually. Instead update the interface, customize and rebuild using the annotation processor to regenerated output with changes.
- Do **not** (normally) check-in generated code in your source repository. If you use git you can configure the .gitignore file for this.

<a name="jumbotron-end"/>

## 1. Using VALJOGen with JavaC compiler:

```Bash
javac -parameters -cp valjogen-annotationprocessor-*XXX*.jar -Acom.fortyoneconcepts.valjogen.SOURCEPATH=SourceDirForYourCode -s DestinationDirForGeneratedSources -d DestinationDirForOutputClasses SourceDirForYourCodeUsingTheAnnotationProcessor.java
```

Notably, the example above makes the annotation processor available on the class path, uses the JDK1.8+ -parameter option to enable parameter names processing by the annotation processor and the -Akey[=value] option to let the processor know the source path.

## 2. Using VALJOGen with Maven:

Use `Maven 3.2.0` or later and add the dependency (*NOTE: THIS IS FOR THE FUTURE - PROJECT ARTIFACTS ARE NOT IN MAVEN CENTRAL YET*):

```Xml
<dependency>
    <groupId>com.fortyoneconcepts.valjogen.annotationprocessor</groupId>
    <artifactId>valjogen-annotationprocessor</artifactId>
    <version>*XXX*</version>
</dependency>
```

In addition you should configure the following for the maven-compiler-plugin:

```Xml
<plugin>
   <groupId>org.apache.maven.plugins</groupId>
   <artifactId>maven-compiler-plugin</artifactId>
   ...
   <configuration>
     <compilerArgs>
       <arg>-parameters</arg>                                                         <!-- Ensure parameter meta data is available for best code generation -->
       <arg>-Acom.fortyoneconcepts.valjogen.SOURCEPATH=${basedir}/src/main/java</arg> <!-- Specify where to locate sources (just an example )-->
     </compilerArgs>
     <enableAssertions>true</enableAssertions>
   </configuration>
</plugin>
```

## 3. Using VALJOGen with Eclipse:

```
In Eclipse open project Properties/Java Compiler/Annoation Processing and enable Annotation processing. Then add a key "com.fortyoneconcepts.valjogen.SOURCEPATH" pointing to the source directories for your project.
```

### Using VALJOGen with Eclipse and Maven *(CURRENTLY UNTESTED)*:

1. Install Eclipse Luna 4.4+
2. Install Eclipse plugin m2e (from eclipses build-in "Luna" update site)
3. Install jbosstools's m2e-apt plugin from (from update site "http://download.jboss.org/jbosstools/updates/m2e-extensions/m2e-apt")

## 4. Customizing generated value classes

VALJOGen output can be customized in a number of ways as listed below (in a order of preference):

1. Use existing options in VALJOGenerate and VALJOConfigure annotations.
2. Specify a BaseClass with your custom code in your VALJOConfigure annotation.
3. SubClass the generates class(es) and out your custom stuff there (use VALJOConfigure modifier option to make generated class abstract or non final)
4. Add custom method(s) in a custom template group file specified in your VALJOConfigure annotation.
5. Override the regions defined in the build-in templates. Do this in a custom template group file specified in your VALJOConfigure annotation.
6. Override existing template methods. Do this in a custom template group file specified in your VALJOConfigure annotation

## 5. Support
- [Main website](http://valjogen.41concepts.com)
- Free [Google group discussions](http://groups.google.com/group/valjogen)
- Paid email support : valjogen (AT) 41concepts (dot) com

## 6. About VALJOGen project internals

See [readme in annotaton processor project](valjogen-processor/README.md) for some implementation details or look at the source.

The [VALJOGen website](http://valjogen.41concepts.com) is generated using [nanoc](http://nanoc.ws/) using [github flavored markdown](https://help.github.com/articles/github-flavored-markdown/) files. See site subproject folder for details. 

/ Morten M. Christensen, [41concepts](http://www.41concepts.com)