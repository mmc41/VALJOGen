/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

import java.util.*;
import java.util.stream.Collectors;

/***
 * Meta-information about an existing method or a method that should be generated (implemented).
 *
 * @author mmc
 */
public class Method extends DefinitionBase
{
	protected final Type declaringType;
	protected final List<Parameter> parameters;
	protected final List<Type> thrownTypes;
	protected final String javaDoc;
	protected final Type returnType;
	protected final EnumSet<Modifier> modifiers;
	protected ImplementationInfo implementationInfo;

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo)
	{
	    this(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, defaultModifiers(clazz.getConfiguration(), declaredModifiers), implementationInfo);
	}

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, ImplementationInfo implementationInfo)
	{
	    super(clazz, methodName, declaredModifiers);
	    this.declaringType = Objects.requireNonNull(declaringType);
		this.parameters = Objects.requireNonNull(parameters);
		this.thrownTypes = Objects.requireNonNull(thrownTypes);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = Objects.requireNonNull(returnType);
		this.modifiers = modifiers;
		this.implementationInfo = implementationInfo;
	}

	private static EnumSet<Modifier> defaultModifiers(Configuration cfg, EnumSet<Modifier> declaredModifiers)
	{
		Set<Modifier> modifiers = new HashSet<>();

		modifiers.add(Modifier.PUBLIC);
		if (declaredModifiers.contains(Modifier.STATIC))
			modifiers.add(Modifier.STATIC);

		if (cfg.isFinalMethodsEnabled())
			modifiers.add(Modifier.FINAL);
		if (cfg.isSynchronizedAccessEnabled())
			modifiers.add(Modifier.SYNCHRONIZED);

		return modifiers.isEmpty() ? EnumSet.noneOf(Modifier.class) : EnumSet.copyOf(modifiers);
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	public ImplementationInfo getImplementationInfo()
	{
		return implementationInfo;
	}

	public void setImplementationInfo(ImplementationInfo implementationInfo)
	{
		this.implementationInfo=implementationInfo;
	}

	public boolean isConstructor()
	{
		return false;
	}

	public boolean isThisReturnType()
	{
		boolean thisReturnType = clazz.getInterfaceTypes().stream().anyMatch(t -> t.equals(returnType));
		return thisReturnType;
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public Type getDeclaringType()
	{
		return declaringType;
	}

	@Override
	public EnumSet<Modifier> getModifiers()
	{
		return modifiers;
	}

	/**
	 * Return The name of the method with unqualified type names in parenthesis. All type names are unqualified so not guarenteed to be unique.
	 *
	 * @return The string suitable for overload resolution.
	 */
	public String getOverloadName()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(name);
		sb.append("(");
		sb.append(parameters.stream().map(p -> {
		  String name = p.getErasedType().getQualifiedName();
		  return getUnqualifiedName(name);
		}).collect(Collectors.joining(",")));
		sb.append(")");

		return sb.toString();
	}

	/**
	 * Return The name of the method with unqualified type names in parenthesis. All type names are unqualified so not guarenteed to be unique.
	 *
	 * @param overLoadName The overload name to compare with
	 *
	 * @return The string suitable for overload resolution.
	 */
	public boolean hasOverLoadName(String overLoadName)
	{
		if (this.getName().contains("compareTo"))
			System.out.println("got here");

		boolean same = this.getOverloadName().equals(overLoadName);
		return same;
	}

	/**
	 * Return The name of the method followed by underscore seperated unqualified type names in parenthesis. All type names are unqualified so not guarenteed to be unique.
	 *
	 * @return The string with the name of the template that corresponds to the method.
	 */
	public String getTemplateName()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("method_");
		sb.append(name);
		if (parameters.size()>0) {
			sb.append("_");
			sb.append(parameters.stream().map(p -> {
			  String name = p.getErasedType().getQualifiedName();
			  return getUnqualifiedName(name);
			}).collect(Collectors.joining("_")));
		}

		return sb.toString();
	}

	public List<Parameter> getParameters()
	{
		return parameters;
	}

	public List<Type> getThrownTypes()
	{
		return thrownTypes;
	}

	public String getJavaDoc()
	{
		return javaDoc;
	}

	@Override
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		String name = isConstructor() ? "Constructor" : "Method";

		sb.append(name+" [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
			sb.append(", declaringType="+declaringType.getName()+", methodName=" + getName() +
					  ", parameters=["+parameters.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator()))+"]"+
					  ", returnType="+returnType.getPrototypicalName() +
					  ", thrownTypes=["+thrownTypes.stream().map(t -> t.getPrototypicalName()).collect(Collectors.joining(","+System.lineSeparator()))+"]"+
					  ", declaredModifiers="+declaredModifiers+
					  ", modifiers="+modifiers+
					  ", implementationInfo="+implementationInfo+"]");

		sb.append("]");

		return sb.toString();
	}
}
