/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.annotations;

import java.lang.annotation.*;
import com.fortyoneconcepts.valjogen.annotations.types.*;

/**
 * Specifies details about the code that should be generated. May be specified on a package (package-info.java) or on a interface alongside
 * the {@link VALJOGenerate} annotation. If specified on both a package and an interface then the package specification is ignored. Has no effect
 * unless affected interfaces also has a {@link VALJOGenerate} annotation. All options may be overruled by setting identically named qualified key/values
 * in the annotation processor using the <b>-A</b>com.fortyoneconcepts.valjogen.<i>optionName</i>=<i>value</i> option to javac. Indeed some details like logLevel
 * are usally better specified as a runtime option instead of hardcoding in the source using this annotation.
 * <p><b>Usage example (package-info.java):</b></p>
 * <pre><code>
 *<span class="identifier">{@literal @}VALJOConfigure</span>(<span class="identifier">outputPackage</span>=<span class="string">"test.impl"</span>, <span class="identifier">baseClazzName</span>=<span class="string">"test.CommonBaseClass"</span>)
 * <span class="keyword">package</span> <span class="identifier">test</span>;
 * </code></pre>
 * <p>
 * The above code will instruct the VALJOGen annotation processor to have generated value object classes from interfaces in this package belong to
 * the "test.impl" package and to inherit from a common base class with qualified name "test.CommonBaseClass". Note that generation requires
 * a seperate {@link VALJOGenerate} annotation. This other annotation is also used to specify what name the generated class should have.
 * </p>
 * <p>
 * For advanced customization do refer to the customJavaTemplateFileName option. This is the ultimate <i>swiss army knife</i> that lets you change and extend <b>everything</b> you want. It is a bit more difficult to use then other features though so look at the other options first.
 * </p>
 *  * All string properties expand system properties as macros when enclosed in $() as well as the following macros:<p>
 * <code>$(This)</code> which resolves to the fully qualified name of the generated class. The macro can be especially useful when implementing the Comparable interface using the extraInterfaceNames option.<p>
 * <code>$(IThis)</code> which resolves to the the fully qualified name of the interface that is used for generation.<p>
 * <code>$(ExecutionDate)</code> which resolves to the the time the annotation processes ran.<p>
 * <code>$(N/A)</code> which signifies the value is unspecified (internally represented as a 'null').
 *
 * <p>
 * @author mmc
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value={ElementType.TYPE, ElementType.PACKAGE})
public @interface VALJOConfigure
{
	/**
	* Package name of generated class. May be overruled by a fully qualified name on VALJOGenerate or by equivalent annotation processor key.
	*
	* If not specified, the output package will be the same as the interface used to generate the class.
	*
	* @return Package name.
	*/
    String outputPackage() default "$(N/A)";

	/**
	* Explicitly defined modifiers such as PUBLIC, PROTECTED, PRIVATE, FINAL or ABSTRACT to set in generated class.
	* If none are present they will be auto-generated with a preference for PUBLIC FINAL or PUBLIC ABSTRACT classes.
	* May be overruled by equivalent annotation processor key.
	*
	* @return Modifiers to apply to the generated class.
	*/
    String[] clazzModifiers() default {};

    /**
	* Linewidth for generated code. 0 if unlimited. May be overruled by equivalent annotation processor key.
	*
	* @return line width to use when generating output.
	*/
    int lineWidth() default -1;

    /**
    * Specifies if generated object should be immutable or mutable. May be overruled by equivalent annotation processor key.
    *
    * Default value is undefined, which means that the annotation processor will guess what the desired mutability (look and see if there are mutable setters).
    *
    * @return The desired mutability of the generated object.
    */
    Mutability mutability() default Mutability.Undefined;

    /**
    * Specifies if generated object should be easily convertable to/from xml or json. May be overruled by equivalent annotation processor key.
    *
    * @return The desired conversion approach to use if any.
    */
    DataConversion dataConversion() default DataConversion.NONE;

	/**
	* Specifies if generated members and method parameters should be final if possible. May be overruled by equivalent annotation processor key.
	*
	* @return True if generated members and method parameters are prefered to be final.
	*/
    boolean finalMembersAndParametersEnabled() default true;

	/**
	* Specifies if generated methods (incl. property methods) should be final. May be overruled by equivalent annotation processor key.
	*
	* @return True if generated methods are prefered to be final
	*/
	boolean finalMethodsEnabled() default false;

	/**
	* Specifies assignments to local variables should guard against null. May be overruled by equivalent annotation processor key.
	*
	* @return True if local variables should be guarded against null assignments
	*/
	boolean ensureNotNullEnabled() default true;

	/**
	* Specifie if a static factory method should be generated instead of having the constructor being public. May be overruled by equivalent annotation processor key.
	*
	* @return True a static factory method should be generated.
	*/
	boolean staticFactoryMethodEnabled() default true;

	/**
	* Specifies if generated properties/methods for mutable members should be synchronized. May be overruled by equivalent annotation processor key.
	*
	* @return True if generated properties/methods are prefered to be synchronized.
	*/
	boolean synchronizedAccessEnabled() default false;

	/**
	* Specifies prefix to use by temporary variables in method in order to avoid clashing with members. May be overruled by equivalent annotation processor key.
	*
	* @return Prefix to use by local variables.
	*/
	String suggestedVariablesPrefix() default "_";

	/**
	* Specifies the serialization ID to use for generated classes or ZERO if not set. Any non-zero value will automatically be inserted into classes that
	* directly or indirectly implement the {@link java.io.Serializable} interface.
	*
	* May be overruled by equivalent annotation processor key.
	*
	* @return The serialization ID to use or '0' if not set.
	*/
	long serialVersionUID() default 1L;

	/**
	* Specifies if equals method should be generated for the class. May be overruled by equivalent annotation processor key.
	*
	* Remember to supply a hash method when generating equals methods.
	*
	* @see VALJOConfigure#hashEnabled
	*
	* @return True if a equals method should be generated for the class.
	*/
	boolean equalsEnabled() default true;

	/**
	* Specifies if hash method should be generated for the class. May be overruled by equivalent annotation processor key.
	*
	* The generated hash method will be consistent with equals if this method is generated as well.
	*
	* @see VALJOConfigure#equalsEnabled
	*
	* @return True if a hash method should be generated for the class.
	*/
	boolean hashEnabled() default true;

	/**
	* For classes that implement the {@link Comparable} interface this is the ordered names of members to use in a compareTo implementation. This may include accessible members of a base class.
	* If left unspecified it defaults to all members of the generated class in declaration order. May be overruled by equivalent annotation processor key.
	*
	* @return Array of names of all members to use in compareTo method and in specified order.
	*/
    String[] comparableMembers() default { };

	/**
	* Specifies if a toString method should be generated the class. May be overruled by equivalent annotation processor key.
	*
	* @return True if a toString method should be generated for the class.
	*/
	boolean toStringEnabled() default true;

	/**
	* Specifies if inheritDoc javaDoc comments should be added to the generated class for methods. May be overruled by equivalent annotation processor key.
	*
	* @return True if sinple javaDoc with inheritDoc reference should be generated for methods on the generated class.
	*/
	boolean insertInheritDocOnMethodsEnabled() default true;

	/**
	* Specifies if errors should be issued for malformed getter and setter methods. May be overruled by equivalent annotation processor key.
	*
	* @return True if malformed getter/setter methods should be ignored. False if they should give errors.
	*/
	boolean ignoreMalformedProperties() default false;

	/**
	* Fully qualified extra classes to import into the generated code (in addition to implemented interfaces
    * and baseclass which is imported as needed).
	*
	* @return Array of all qualified classes to import.
	*/
    String[] importClasses() default { "java.util.Arrays", "java.util.Objects", "javax.annotation.Generated" };

    /**
	* Specifies the prefixes of javaBean-style getter methods. Governs which memebers are inserted into the target class and which property method are
	* implemented. By default only standard javaBean prefixes are included but additional custom prefixes can be added.
	* Prefixes may be overruled by equivalent annotation processor key.
	*
	* @return Array of prefixes for getter methods.
	*/
    String[] getterPrefixes() default { "is", "get" };

    /**
	* Specifies the prefixes of javaBean-style getter methods. Governs which memebers are inserted into the target class and which property method are
	* implemented. By default only the standard javaBean prefix is included but additional custom prefixes can be added. For instance "withXXX" methods
	* for immutable properties might be useful. Prefixes may be overruled by equivalent annotation processor key.
	*
	* @return Array of prefixes for setter methods.
	*/
    String[] setterPrefixes() default { "set" };

	/**
	* Specifies if return type of immutable setters should be fixed to be the class itself (covariannce) instead of what interfaces declare. May be overruled by equivalent annotation processor key.
	*
	* @return True if return types of immutable setters should always be the implementation class.
	*/
    boolean forceThisAsImmutableSetterReturnType() default true;

	/**
	* Specifies if additional interfaces should be implemented to the generated class. May be overruled by equivalent annotation processor key.
	*
	* Hint: For generic interfaces using the macro <code>"$(This)"</code> for the generic qualifier is useful to refer to the name of the generated class. F.x.
	* to implement Comparable write "<code>java.lang.Comparable&lt;(This)&gt;</code>".
	*
	* @return Array of all additional interfaces
	*/
    String[] extraInterfaceNames() default {};

	/**
	* Specifies the base class of the generated class. May be overruled by equivalent annotation processor key.
	*
	* Note: The base class must be serializable in itself for serialization of the generated class to work (serialization does not require a default constructor however).
	*
	* @return Name of the base class for the generated class.
	*/
    String baseClazzName() default "java.lang.Object";

	/**
	* Specifies the base class constructors to delegate to in generated constructors and factory methods. Each generated constructor will include arguments for the corresponding base constructor followed
	* by the arguments for the members in the class. By default ALL base constructors will be used. As factory methods (if enabled) follow constructors a corresponding set of factory methods will be
	* generated as well.
	*
	* The specifiers are of form <code>"(" &lt;unqualifed parameter type name&gt; { "," &lt;unqualifed parameter type name&gt; } ")"</code>. For example to specify a constructor taking an integer and a String
	* as arguments write <code>(int,String)</code>. Note that all type names are unqualified.
	*
	* In addition the wildcard '*' may be used to match a single type name and the wildcard '**' may used to match all set of typenames (i.e. each and every base constructor).
	*
	* @return List of base class constructors to use when generating constructors for the class.
	*/
    String[] baseClazzConstructors() default { "(**)" };

	/**
	* Specified annotation strings with arguments that should be added to the generated class.
	*
	* @return annotation string to add to the generated class.
	*/
    String[] clazzAnnotations() default { "@Generated(value = \"com.fortyoneconcepts.valjogen\", date=\"$(ExecutionDate)\", comments=\"Generated by ValjoGen code generator (ValjoGen.41concepts.com) from $(IThis)\")" };

	/**
	* Specified annotation strings with arguments that should be added to constructor of the generated class.
	*
	* @return annotation string to add to the constructor of the generated class.
	*/
    String[] constructorAnnotations() default {};

	/**
	* Specified annotation strings with arguments that should be added to factory method of the generated class.
	*
	* @return annotation string to add to the constructor of the factory method of the generated class class.
	*/
    String[] factoryMethodAnnotations() default {};

    /**
	* Javadoc text string (without leading/ending comment characters) that should be added to generated class.
	*
	* @return Javadoc text to add to the generated class.
	*/
    String clazzJavaDoc() default "$(N/A)";

    /**
	* UTF-8 formatted file that contains a header that should be added at the top of the generated output.
	*
	* @return Filename of text file containing header.
	*/
    String headerFileName() default "$(N/A)";

    /**
	* This is the <b>ultimate customization power feature</b> as it allows all aspects of the generated class to be changed or extended. To enable this feature supply the name of a UTF-8 formatted
	* StringTemplate 4 (ST) group file that should be used. It can be bit more difficult to use compared to other features of this tool, so do look at the other options first.
	* <p>
	* See <a href="http://www.stringtemplate.org/" target="_blank">ST 4 documentation</a> and the <a href="https://theantlrguy.atlassian.net/wiki/display/ST4/StringTemplate+cheat+sheet" target="_blank">cheat sheet</a> in
	* particular for details about how to write templates. The custom group file that can be added using this option will inherit from the existing templates allowing you to add new templates
	* or override the build-in templates.
	* <p>
	* Note that ST maintains strict Model-View separation so templates can not contain logic or compare values other then booleans. Note also that templates can call into model getters but with the <b>get</b>/<b>is</b> prefix omitted.
	* <p>
	* Refer to the <a href="http://github.com/41concepts/VALJOGen/tree/master/valjogen-processor/src/main/resources/templates" target="_blank">existing source (*.stg files)</a>
	* for this processor for how to work with the models available from a template. For details about the model see refer to javadoc or source for the <b>com.fortyoneconcepts.valjogen.model.*</b>
	* classes in the annotation processor tool.
	* <p>
	* Preferably consider overriding declared ST regions like for example <code>&lt;{@literal @}preamble&gt;</code> to change output rather then overriding existing templates in your ST file. This
	* should reduce maintaince problems for future updates. ST requires regions to be prefixed with the template method they are declared in, so to add code in beginning of the equals method, add a
	* ST file with a template rule like this:
	* <pre><code>
	* {@literal @}<span class="st-identifier">method_equals</span>.<span class="st-identifier">preamble</span>() ::= &lt;&lt; <span class="free-comment">// initial method code here.</span> &gt;&gt;
	* </code></pre>
	* <p>
	* When you need to generate code for new java methods implementations, first make sure the method signature is declared, then add a template named <code>method_<i>specifier</i></code>
	* with the template arguments <code>clazz</code> and <code>method</code> in your ST group file. The specifier is the java method name followed by underscore seperated, unqualified type arguments with a leading underscore
	* before first argument (if present). For example to generate the method "void f()" create a template named <code>method_f</code>" and to generate the method "double f(String arg, int value)"
	* create a template named <code>method_f_String_int</code>". The require custom method arguments are of type Clazz and Method in the com.fortyoneconcepts.valjogen.model package. Refer to the JavaDoc for these for details.
	*
	* Declared methods that the implementation can override include methods in the interfaces and base class and their ancestors. In addition, if the generated class is serializable
	* the magic serialization methods readResolve, readObjectNoData, writeObject and writeReplace are also recognized as methods.
	*
	* In you instead need to implement non-declared methods, add nested types or do something special then do override one of the ST regions in the main <code>class</code> template like f.x. {@literal @}class.before_class_methods,
	* {@literal @}class.after_class_methods, {@literal @}class.before_instance_methods or {@literal @}class.after_instance_methods. When you override regions you have complete freedom to add
	* any kind of code but without help like method signatures etc.
	*
	* The example shown below shows how the main part of the equals method might be defined. Refer to the ST <a href="http://github.com/41concepts/VALJOGen/blob/master/valjogen-processor/src/main/resources/templates/equals.stg" target="_blank">source file for the equals method</a>
	* for the complete and present source code. In any case it is a good idea to study the template sources and example templates for tips about how to write custom templates. Note how getters in the model
	* is accessed (with getter prefixes stripped) to facilitate the generation of the method according of the actal class members, method arguments etc.
	* <pre><code>
	* <span class="st-identifier">method_equals</span>(<span class="st-identifier">clazz</span>, <span class="st-identifier">method</span>) ::= &lt;&lt;
	*  &lt;<span class="st-identifier">declare_method</span>(<span class="st-identifier">clazz</span>, <span class="st-identifier">method</span>)&gt;
	*  {
	*     &lt;<span class="st-keyword">if</span>(<span class="st-identifier">clazz</span>.<span class="identifier">anyMembers</span>)&gt;
	*     &lt;<span class="st-identifier">clazz</span>.<span class="identifier">prototypicalName</span>&gt; &lt;<span class="st-identifier">uniqueVariableName</span>(<span class="st-identifier">clazz</span>,<span class="string">"other"</span>)&gt; = (&lt;<span class="st-identifier">clazz</span>.prototypicalName&gt;) &lt;<span class="st-keyword">first</span>(<span class="st-identifier">method</span>.<span class="identifier">parameters</span>).<span class="identifier">name</span>&gt;;
	*
    *     <span class="keyword">return</span> (&lt;<span class="st-identifier">clazz</span>.<span class="identifier">members</span>:{<span class="st-identifier">m</span> | &lt;(<span class="st-identifier">equalsTemplateNamesByTypeCategory</span>.(<span class="st-identifier">m</span>.<span class="identifier">type.typeCategory</span>))(<span class="st-identifier">clazz</span>, <span class="st-identifier">m</span>.<span class="identifier">name</span>, <span class="st-identifier">m</span>.<span class="identifier">type</span>)&gt;}; <span class="st-keyword">wrap</span>, <span class="st-keyword">anchor</span>, <span class="st-keyword">separator</span>=" &amp;&amp; "&gt;);
	*     &lt;<span class="st-keyword">else</span>&gt;
	*     <span class="keyword">return true</span>;
    *     &lt;<span class="st-keyword">endif</span>&gt;
	*  }
	* &gt;&gt;
	* </code></pre>
	*
	* This file name may be overruled by equivalent annotation processor key.
	* <p>
	*
	* @return Filename of string template group file.
	*/
    String customJavaTemplateFileName() default "$(N/A)";

    /**
    * Specifies if the annotation processor should warn about parameter names that are synthesised because -parameter option is missing.
    *
    * @return True if annotation processor should warn about missing -parameter option.
    */
    boolean warnAboutSynthesisedNames() default true;

    /**
	* Specifies the {@link java.util.logging.Level} log level to use inside the annotation processor. Set this to INFO or FINE to inspect model instances,
	* inspect output or to help track errors inside the processor. Set to WARNING otherwise.
	*
	* Note that normally java.util.logging.ConsoleHandler.level needs to be set as well for log levels below INFO to be shown.
	*
	* @return Log level
	*/
    String logLevel() default "WARNING";

    /**
	* Experimental debugging feature that specifies if the annotation processor should open the STViz GUI Inspector for debugging the internal stringtemplates. You should not need to enable this unless you are
	* adding/modifying code templates and run into problems. Be aware that code generation will pause while the generator is shown - you properly do not want to do that when using an IDE.
	* May be overruled by equivalent annotation processor key.
	*
	* @return True if annotation processor should open StringTemplagte's STViz Gui explorer during code generation phase.
	*/
    boolean debugStringTemplates() default false;

	/**
	*  Experimental IETF BCP 47 language tag string descripting internal locale to use for annotation processor. Has undefined behavior. May be overruled by equivalent annotation processor key.
	*
	*  @return Internal language tag describing locale to use for annotation processor.
	*
	*  @see java.util.Locale#forLanguageTag
	*/
    String localeTag() default "en-US";

    /**
	* An optional user supplied comment. Not used for anything by default but could potentially be used in a custom template or by a build tool. It is up to you what to use this option for.
	*
	* @return An optional comment.
	*/
    String comment() default "$(N/A)";
}
