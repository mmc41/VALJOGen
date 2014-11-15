/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;

/**
 * Meta-information about a formal parameter for a method/property.
 *
 * @author mmc
 */
public class Parameter extends DefinitionBase implements TypedModel
{
	protected final Type type;
	protected final Type erasedParamType;

	public Parameter(BasicClazz clazz, Type paramType, Type erasedParamType, String paramName, EnumSet<Modifier> declaredModifiers, List<Annotation> annotations)
	{
		super(clazz, paramName, declaredModifiers, annotations);
		this.type=Objects.requireNonNull(paramType);
		this.erasedParamType=Objects.requireNonNull(erasedParamType);
	}

	public Parameter(BasicClazz clazz, Type paramType, String paramName, EnumSet<Modifier> declaredModifiers, List<Annotation> annotations)
	{
		super(clazz, paramName, declaredModifiers, annotations);
		this.type=Objects.requireNonNull(paramType);
		this.erasedParamType=Objects.requireNonNull(paramType);
	}

	public boolean isDelegating()
	{
		return false;
	}

	public boolean isMemberAssociated()
	{
		return false;
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public EnumSet<Modifier> getModifiers()
	{
		return (clazz.getConfiguration().isFinalMembersAndParametersEnabled()) ? EnumSet.of(Modifier.FINAL) : EnumSet.noneOf(Modifier.class);
	}

	// TODO: Remove this setter.
	public Parameter setName(String newParamName)
	{
		return new Parameter(clazz, type, erasedParamType, newParamName, declaredModifiers, annotations);
	}

	@Override
	public Type getType()
	{
	    return type;
	}

	public Type getErasedType()
	{
	    return erasedParamType;
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

		writer.print(this.getClass().getSimpleName()+"(this=@"+ Integer.toHexString(System.identityHashCode(this))+", name="+ getName()+", type="+type.getPrototypicalName()+", erasedType=" + erasedParamType.getPrototypicalName() +", declaredModifiers="+declaredModifiers+", modifiers="+getModifiers());

		if (annotations.size()>0) {
		  writer.ensureNewLine();
		  writer.print("annotations= [");
		  annotations.stream().forEach(p -> p.print(writer, detailLevel+1));
		  writer.println("]");
		}

		printExtraTop(writer, detailLevel);

		printExtraBottom(writer, detailLevel);

		writer.println(")");

		if (detailLevel>0)
			writer.decreaseIndent();
	}
}
