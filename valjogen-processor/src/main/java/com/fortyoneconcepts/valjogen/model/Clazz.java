/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Information about the java "class" that need to be generated. Refers to other model elements like members, properties, methods, types etc.
 *
 * Fully independent of javax.model.* classes even though {@link com.fortyoneconcepts.valjogen.processor.ClazzFactory} is the primary
 * way to create Clazz instances from javax.model.* classes (provided by an annotation processor).
 *
 * @author mmc
 */
public final class Clazz extends ObjectType implements Model
{
	private final Configuration configuration;

	private final String packageName;
	private final String javaDoc;

	private List<Type> importTypes;
	private List<Member> members;
	private List<Property> properties;
	private List<Method> methods;

	private boolean initializedContent;

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
		super(qualifiedClassName);
		super.clazzUsingType=this;
		super.helperTypes = new HelperTypes(this);

		this.configuration = Objects.requireNonNull(configuration);
		this.interfaceTypes = new ArrayList<Type>();
		this.baseClazzType = new NoType(this);
		this.javaDoc = Objects.requireNonNull(javaDoc);

		this.packageName = getPackageFromQualifiedName(qualifiedClassName);

		this.properties = new ArrayList<Property>();
		this.methods = new ArrayList<Method>();
		this.members = new ArrayList<Member>();
		this.importTypes = new ArrayList<Type>();

		initializedContent=false;
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

	@Override
	public HelperTypes getHelperTypes()
	{
		return helperTypes;
	}

	@Override
	public boolean initialized()
	{
		return initializedType && initializedContent;
	}

	@Override
	public boolean isSelfType()
	{
		return true;
	}

	/**
     * Nb. Post-constructor for what is inside the class such as methods, members etc. + imports. Both this method and the super class'es {@link ObjectType#initType}
     * methods must be called for the class to be fully initialized and ready for use. Must be called only once.
     *
	 * @param members Member variables for class.
	 * @param properties Property methods for class.
	 * @param nonPropertyMethods Other methods for class.
	 * @param importTypes Types to be imported for class.
	 */
	public void initContent(List<Member> members, List<Property> properties, List<Method> nonPropertyMethods, List<Type> importTypes)
	{
		if (initializedContent)
			throw new IllegalStateException("Clazz content already initialized");

		this.importTypes=Objects.requireNonNull(importTypes);

        this.members=Objects.requireNonNull(members);

        this.properties=Objects.requireNonNull(properties);

        this.methods=Objects.requireNonNull(nonPropertyMethods);

        initializedContent=true;
	}

	public boolean hasGenericQualifier()
	{
		return !getGenericQualifierText().isEmpty();
	}

	public String getGenericQualifierText()
	{
		return getGenericQualifier(qualifiedProtoTypicalTypeName);
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
		assert initialized() : "Class initialization missing";
		return !methods.stream().allMatch(m -> m.implementationClaimed);
	}

	public boolean isSynchronized()
	{
		assert initialized() : "Class initialization missing";
		return getConfiguration().isSynchronizedAccessEnabled() &&  members.stream().anyMatch(member -> !member.isFinal());
	}

	public boolean hasPrimitiveMembers()
	{
		assert initialized() : "Class initialization missing";
		return members.stream().anyMatch(m -> m.getType().isPrimitive());
	}

	public boolean hasArrayMembers()
	{
		assert initialized() : "Class initialization missing";
		return members.stream().anyMatch(m -> m.getType().isArray());
	}

	public List<Member> getMembers()
	{
		assert initialized() : "Class initialization missing";
		return members;
	}

	public boolean hasAnyMembers()
	{
		assert initialized() : "Class initialization missing";
		return !members.isEmpty();
	}

	public List<Property> getPropertyMethods()
	{
		assert initialized() : "Class initialization missing";
		return properties;
	}

	public List<Method> getMethods()
	{
		assert initialized() : "Class initialization missing";
		return methods;
	}

	public List<Method> getClaimedImplementationMethods()
	{
		return methods.stream().filter(m -> m.implementationClaimed).collect(Collectors.toList());
	}

	public List<Type> getImportTypes()
	{
		assert initialized() : "Class initialization missing";
		return importTypes;
	}

	@Override
	public int hashCode()
	{
		return qualifiedProtoTypicalTypeName.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return (this==obj);
	}

	@Override
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Clazz [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
		{
			sb.append(", initialized="+initialized()+", qualifiedClassName="+ qualifiedProtoTypicalTypeName);
		}

		// Specific to class, most details are only printed as top level:
		if (level==0)
		{
			sb.append(" packageName=" + packageName + System.lineSeparator()
					 +", base type=" + baseClazzType.toString(level+1)
					 + System.lineSeparator() + ", interface interfaceTypes=["
					 + interfaceTypes.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator()))+"]"+ System.lineSeparator()+ ", interfaceTypesWithAscendants=["
					 + interfaceTypesWithAscendants.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator())) +"]"+ System.lineSeparator()
					 + ", genericTypeArguments="+ToStringUtil.toString(genericTypeArguments, level+1)+System.lineSeparator()
					 + ", members="+ToStringUtil.toString(members, level+1)+System.lineSeparator()
					 + ", properties=" + ToStringUtil.toString(properties,level+1)+System.lineSeparator()
					 + ", methods="+ToStringUtil.toString(methods,level+1)+System.lineSeparator()
					 + ", configuration="+configuration+"]"+System.lineSeparator());
		}

		sb.append("]");

		return sb.toString();
	}
}
