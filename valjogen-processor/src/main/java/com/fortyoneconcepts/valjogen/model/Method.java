/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

import java.util.*;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.processor.STUtil;
import com.fortyoneconcepts.valjogen.processor.TemplateKind;

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
	protected final String templateName;

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo)
	{
	    this(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, defaultModifiers(clazz.getConfiguration(), declaredModifiers), implementationInfo, TemplateKind.TYPED);
	}

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo, TemplateKind templateKind)
	{
	    this(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, defaultModifiers(clazz.getConfiguration(), declaredModifiers), implementationInfo, templateKind);
	}

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, ImplementationInfo implementationInfo)
	{
	    this(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, modifiers, implementationInfo, TemplateKind.TYPED);
	}

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, ImplementationInfo implementationInfo, TemplateKind templateKind)
	{
	    super(clazz, methodName, declaredModifiers);
	    this.declaringType = Objects.requireNonNull(declaringType);
		this.parameters = Objects.requireNonNull(parameters);
		this.thrownTypes = Objects.requireNonNull(thrownTypes);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = Objects.requireNonNull(returnType);
		this.modifiers = modifiers;
		this.implementationInfo = implementationInfo;

		switch(templateKind)
		{
		  case TYPED: this.templateName = STUtil.getTypedTemplateName(name, parameters.stream().map(p -> p.getErasedType().getQualifiedName())); break;
		  case UNTYPED: this.templateName = STUtil.getUnTypedTemplateName(name); break;
		  case CONSTRUCTOR: this.templateName = STUtil.getConstructorTemplateName(methodName); break;
		  case PROPERTY: this.templateName = STUtil.getPropertyTemplateName(methodName); break;
		  default: throw new IllegalArgumentException("Unknown templateKind "+templateKind);

		}
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

	public String getQualifiedName()
	{
		return clazz.getName()+"."+getName();
	}

	/**
	 * Return The name of the method with unqualified type names in parenthesis. All type names are unqualified so not guarenteed to be unique.
	 *
	 * @return The string suitable for overload resolution.
	 */
	public String getOverloadName()
	{
		return getOverloadName(true);
	}

	protected String getOverloadName(boolean includeMethodName)
	{
		StringBuilder sb = new StringBuilder();

		if (includeMethodName)
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
	 * Return The name of the method followed by underscore seperated unqualified type names in parenthesis. All type names are unqualified so not guarenteed to be unique.
	 *
	 * @return The string with the name of the template that corresponds to the method.
	 */
	public final String getTemplateName()
	{
		return templateName;
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

	public boolean isDelegating()
	{
		return false;
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
