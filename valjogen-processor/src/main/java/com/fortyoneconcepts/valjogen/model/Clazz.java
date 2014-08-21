/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Information about the java "class" that need to be generated. Refers to other model elements like members, properties, methods, types etc.
 *
 * Fully independent of javax.model.* classes even though {@link com.fortyoneconcepts.valjogen.processor.ClazzFactory} is the primary
 * way to create Clazz instances from javax.model.* classes (provided by an annotation processor).
 *
 * Nb. Unlike other models, this class is mutable as some of the externally constructed instance members need
 *     to be constructed after this class so they can refer back to this instance.
 *
 * @author mmc
 */
public final class Clazz implements Model
{
	private final Configuration configuration;
	private final String packageName;
	private final String qualifiedClassName;
	private final String javaDoc;

	private List<Type> interfaceTypes;
	private Type baseClazzType;

	private List<Type> importTypes;
	private List<Member> members;
	private List<Property> properties;
	private List<Method> methods;
	private HelperTypes helperTypes;

	/**
	 * Constructs a prelimiary Clazz instance from a configuration with only a few values such as name specificed in advanced. After constructing the instance, the various
	 * setters must be used to finish initialization.
	 *
	 * @param configuration The configuration of how generated code should look.
	 * @param qualifiedClassName The full name of the class that should be generated.
	 * @param javaDoc JavaDoc if any.
	 */
	public Clazz(Configuration configuration, String qualifiedClassName, String javaDoc)
	{
		this.configuration = Objects.requireNonNull(configuration);
		this.qualifiedClassName = Objects.requireNonNull(qualifiedClassName);
		this.interfaceTypes = new ArrayList<Type>();
		this.baseClazzType = null;
		this.javaDoc = Objects.requireNonNull(javaDoc);

		this.packageName = getPackageFromQualifiedName(qualifiedClassName);

		this.properties = new ArrayList<Property>();
		this.methods = new ArrayList<Method>();
		this.members = new ArrayList<Member>();
		this.importTypes = new ArrayList<Type>();
		this.helperTypes = null;
	}

	@Override
	public Configuration getConfiguration()
	{
		return configuration;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public Clazz getClazz()
	{
		return this;
	}

	/**
	 * Returns a class type name but without package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The prototypical class type name without any package.
	 */
	public String getPrototypicalName()
	{
		return getUnqualifiedName(getPrototypicalFullName());
	}

	/**
	 * Returns a simple class type name without package and without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The simple class type name
	 */
	public String getName()
	{
		return stripGenericQualifier(getPrototypicalName());
	}

	/**
	 * Returns a class type name with package but without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The qualified class type name
	 */
	public String getQualifiedName()
	{
		return stripGenericQualifier(getPrototypicalFullName());
	}

	/**
	 * Returns a full class type name with package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The fully qualifid prototypical class type name.
	 */
	public String getPrototypicalFullName() {
		return qualifiedClassName;
	}

	public boolean hasGenericQualifier()
	{
		return !getGenericQualifierText().isEmpty();
	}

	public String getGenericQualifierText()
	{
		return getGenericQualifier(qualifiedClassName);
	}


	public List<Type> getInterfaceTypes()
	{
		return interfaceTypes;
	}

	public void setInterfaceTypes(List<Type> interfaceTypes)
	{
		this.interfaceTypes=Objects.requireNonNull(interfaceTypes);
	}

	public Type getBaseClazzType()
	{
		return Objects.requireNonNull(baseClazzType);
	}

	public void setBaseClazzType(Type baseClazzType)
	{
		this.baseClazzType=Objects.requireNonNull(baseClazzType);
	}

	public String getJavaDoc()
	{
		return javaDoc;
	}

	public boolean isFinal()
	{
		return !isAbstract() && getConfiguration().isFinalClassEnabled();
	}

	public boolean isAbstract()
	{
		return !methods.isEmpty();
	}

	public boolean isSynchronized()
	{
		return getConfiguration().isSynchronizedAccessEnabled() &&  members.stream().anyMatch(member -> !member.isFinal());
	}

	public boolean hasPrimitiveMembers()
	{
		return members.stream().anyMatch(m -> m.getType().isPrimitive());
	}

	public boolean hasArrayMembers()
	{
		return members.stream().anyMatch(m -> m.getType().isArray());
	}

    public void setMembers(List<Member> members)
    {
        this.members=Objects.requireNonNull(members);
    }

	public List<Member> getMembers() {
		return members;
	}

	public boolean hasAnyMembers()
	{
		return !members.isEmpty();
	}

    public void setPropertyMethods(List<Property> properties)
    {
        this.properties=Objects.requireNonNull(properties);
    }

	public List<Property> getPropertyMethods() {
		return properties;
	}

	public void setNonPropertyMethods(List<Method> methods)
    {
        this.methods=Objects.requireNonNull(methods);
    }

	public List<Method> getNonPropertyMethods()
	{
		return methods;
	}

	public void setImportTypes(List<Type> importTypes)
    {
        this.importTypes=Objects.requireNonNull(importTypes);
    }

	public List<Type> getImportTypes()
	{
		return importTypes;
	}

	public void setHelperTypes(HelperTypes helperTypes)
	{
		this.helperTypes=Objects.requireNonNull(helperTypes);
	}

	public HelperTypes getHelperTypes()
	{
		return Objects.requireNonNull(helperTypes, "HelperTypes unavailable");
	}

	@Override
	public int hashCode() {
		return qualifiedClassName.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return (this==obj);
	}

	@Override
	public String toString() {
		return "Clazz [packageName=" + packageName + ", qualifiedClassName="
				+ qualifiedClassName + ", base type=" + getBaseClazzType().getName() + ", interface types="
				+ getInterfaceTypes().stream().map(t -> t.getName()).collect(Collectors.joining(", ")) + ", properties=" + properties + "]";
	}
}
