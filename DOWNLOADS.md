<a name="jumbotron-start"/>

# Downloads

The <abbr title="Value Java Object Generator">VALJOGen</abbr> distribution can be used with maven by using the following dependency

```Xml
<dependency>
  <groupId>com.41concepts</groupId>
  <artifactId>valjogen-annotationprocessor</artifactId>
  <version>1.0.0-RC3</version>
  <optional>true</optional>
</dependency>
```

or you can download the following distribution files manually:

+ [valjogen-annotationprocessor-1.0.0-RC3.jar](http://search.maven.org/remotecontent?filepath=com/41concepts/valjogen-annotationprocessor/1.0.0-RC3/valjogen-annotationprocessor-1.0.0-RC3.jar)
+ [valjogen-annotationprocessor-1.0.0-RC3-javadoc.jar](http://search.maven.org/remotecontent?filepath=com/41concepts/valjogen-annotationprocessor/1.0.0-RC3/valjogen-annotationprocessor-1.0.0-RC3-javadoc.jar)
+ [valjogen-annotationprocessor-1.0.0-RC3-sources.jar](http://search.maven.org/remotecontent?filepath=com/41concepts/valjogen-annotationprocessor/1.0.0-RC3/valjogen-annotationprocessor-1.0.0-RC3-sources.jar)
+ [valjogen-annotations-1.0.0-RC3.jar](http://search.maven.org/remotecontent?filepath=com/41concepts/valjogen-annotations/1.0.0-RC3/valjogen-annotations-1.0.0-RC3.jar)
+ [valjogen-annotations-1.0.0-RC3-javadoc.jar](http://search.maven.org/remotecontent?filepath=com/41concepts/valjogen-annotations/1.0.0-RC3/valjogen-annotations-1.0.0-RC3-javadoc.jar)
+ [valjogen-annotations-1.0.0-RC3-sources.jar](http://search.maven.org/remotecontent?filepath=com/41concepts/valjogen-annotations/1.0.0-RC3/valjogen-annotations-1.0.0-RC3-javadoc.jar)

<a name="jumbotron-end"/>

The dependency on the anntation processor is compile-time only. Unfortunately, there is no good way to specify this with maven but you might get succes marking the dependency as optional.

The single jar for the annotation processor to use in your projects is **valjogen-annotationprocessor-1.0.0-RC3.jar**. Alternatively you may use the valjogen-annotations-1.0.0-RC3.jar if you want to compile your project with the VALJOGen annotations but without the annotation processor.

/ Morten M. Christensen, [41concepts](http://www.41concepts.com)