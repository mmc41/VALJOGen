/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

import java.util.*;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;
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
	protected final String templateName;

	protected ImplementationInfo implementationInfo;
	protected Optional<Method> overriddenByMethod;
	protected Optional<Method> overridesMethod;

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, List<Annotation> annotations, ImplementationInfo implementationInfo, TemplateKind templateKind)
	{
	    this(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, defaultModifiers(clazz.getConfiguration(), declaredModifiers), annotations, implementationInfo, templateKind);
	}

	public Method(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, List<Annotation> annotations, ImplementationInfo implementationInfo, TemplateKind templateKind)
	{
	    super(clazz, methodName, declaredModifiers, annotations);
	    this.declaringType = Objects.requireNonNull(declaringType);
		this.parameters = Objects.requireNonNull(parameters);
		this.thrownTypes = Objects.requireNonNull(thrownTypes);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = Objects.requireNonNull(returnType);
		this.modifiers = modifiers;
		this.implementationInfo = implementationInfo;

		this.overriddenByMethod = Optional.empty();
		this.overridesMethod = Optional.empty();

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
		this.implementationInfo=Objects.requireNonNull(implementationInfo);
	}

	public void setOverriddenByMethod(Method overriddenByMethod)
	{
		this.overriddenByMethod=Optional.of(Objects.requireNonNull(overriddenByMethod));
		overriddenByMethod.overridesMethod=Optional.of(this);
	}

	public Optional<Method> getOverriddenByMethod()
	{
		return overriddenByMethod;
	}

	public Optional<Method> getOverridesMethod()
	{
		return overridesMethod;
	}

	public final boolean isOverridden()
	{
		return getOverriddenByMethod().isPresent();
	}

	/**
	 * Checks if the method is a static factory method.
	 *
	 * @return True if the method is a factory method.
	 */
	public boolean isFactoryMethod()
	{
		return false;
	}

	/**
	 * Checks if the method is a constructor method.
	 *
	 * @return True if the method is a factory method.
	 */
	public boolean isConstructor()
	{
		return false;
	}

	/**
	 * Checks if the method is the most complete creation method and thus the one to use for construction .
	 *
	 * @return True if the method is a primary method to use for construction.
	 */
	public boolean isPrimary()
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

	public boolean isDeclared()
	{
		return declaringType.getClass()!=NoType.class;
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
		return getOverloadName(name, parameters);
	}

	public static final String getOverloadName(String methodName, List<Parameter> parameters)
	{
		StringBuilder sb = new StringBuilder();

		sb.append(methodName);

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
	public void print(IndentedPrintWriter writer, int detailLevel)
	{
		if (detailLevel>=MAX_RECURSIVE_LEVEL) {
			writer.print(name+" ");
			return;
		}

		if (detailLevel>0)
			writer.increaseIndent();

		writer.ensureNewLine();

		writer.print(this.getClass().getSimpleName()+"(this=@"+ Integer.toHexString(System.identityHashCode(this))+", name="+ getName()+" declaringType="+declaringType.getPrototypicalName()
				     +", returnType="+returnType.getPrototypicalName()+", thrownTypes=["+thrownTypes.stream().map(t -> t.getPrototypicalName()).collect(Collectors.joining(","))+"]"
				     +", overridden="+this.isOverridden()+", declaredModifiers="+declaredModifiers+", modifiers="+modifiers+", implementationInfo="+implementationInfo
				     +")");

		printExtraTop(writer, detailLevel);

		writer.increaseIndent();

		if (annotations.size()>0) {
		  writer.ensureNewLine();
		  writer.print("annotations= [");
		  annotations.stream().forEach(p -> p.print(writer, detailLevel+1));
		  writer.println("]");
		}

		if (parameters.size()>0) {
		  writer.ensureNewLine();
		  writer.print("parameters= [");
		  parameters.stream().forEach(p -> p.print(writer, detailLevel+1));
		  writer.println("]");
		}

		printExtraBottom(writer, detailLevel);

		writer.decreaseIndent();
		writer.println(")");

		if (detailLevel>0)
			writer.decreaseIndent();
	}
}
