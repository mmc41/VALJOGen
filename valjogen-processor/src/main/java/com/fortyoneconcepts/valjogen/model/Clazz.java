/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Meta-information about class that need to be generated.
 *
 * @author mmc
 */
public final class Clazz implements Model
{
	private final Configuration configuration;

	private final Elements elements;
	private final Types types;

	private final String packageName;
	private final String qualifiedClassName;
	private final Type interfaceType;
	private final Type baseClazzType;

	protected final String javaDoc;

	private List<Member> members;
	private List<Property> properties;
	private List<Method> methods;

	public Clazz(Configuration configuration, Types types, Elements elements, String qualifiedClassName, TypeMirror interfaceType, TypeMirror baseClass, String javaDoc)
	{
		this.configuration = Objects.requireNonNull(configuration);
		this.elements = Objects.requireNonNull(elements);
		this.types = Objects.requireNonNull(types);
		this.qualifiedClassName = Objects.requireNonNull(qualifiedClassName);
		this.interfaceType = new Type(this, Objects.requireNonNull(interfaceType));
		this.baseClazzType = new Type(this, Objects.requireNonNull(baseClass));
		this.javaDoc = Objects.requireNonNull(javaDoc);

		this.packageName = NamesUtil.getPackageFromQualifiedName(qualifiedClassName);

		this.properties = new ArrayList<Property>();
		this.methods = new ArrayList<Method>();
		this.members = new ArrayList<Member>();
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

	Types getTypes()
	{
		return types;
	}

	Elements getElements()
	{
		return elements;
	}

	/**
	 * Returns a class type name but without package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The prototypical class type name without any package.
	 */
	public String getPrototypicalName()
	{
		return NamesUtil.getUnqualifiedName(getPrototypicalFullName());
	}

	/**
	 * Returns a simple class type name without package and without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The simple classs type name
	 */
	public String getName()
	{
		return NamesUtil.stripGenericQualifier(getPrototypicalName());
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
		return NamesUtil.getGenericQualifier(qualifiedClassName);
	}

	public Type getInterfaceType()
	{
		return interfaceType;
	}

	public Type getBaseClazzType()
	{
		return baseClazzType;
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
				+ qualifiedClassName + ", base type=" + getBaseClazzType().getName() + ", interface type="
				+ getInterfaceType().getName() + ", properties=" + properties + "]";
	}
}
