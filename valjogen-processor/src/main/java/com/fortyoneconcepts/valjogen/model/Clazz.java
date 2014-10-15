/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.model.util.ThrowingFunction;
import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

/**
 * Information about the java "class" that need to be generated including additional information about methods that acts as properties.
 * Together with its supertypes it refers to other model elements like members, properties, methods, types etc.
 *
 * @author mmc
 */
public final class Clazz extends BasicClazz implements Model
{
	private final String qualifiedMaster;
	private final String javaDoc;
	private final String fileHeaderText;

	private List<Property> properties;
	private List<Type> importTypes;
	private List<Member> chosenComparableMembers;
	private EnumSet<Modifier> modifiers;

	/**
	 * Constructs a prelimiary Clazz instance from a configuration with only a few values such as name specificed in advanced. After constructing the instance, the various
	 * setters must be used to finish initialization.
	 *
	 * @param configuration The configuration of how generated code should look.
	 * @param qualifiedClassName The full name of the class that should be generated.
	 * @param qualifiedMaster The fill name of the item this class was generated from.
	 * @param javaDoc JavaDoc if any.
	 * @param fileHeaderText Text to output as header for file(s).
	 * @param helperFactoryMethod Method that can generate helper types for this class.
	 * @throws Exception Exception if could not construct clazz.
	 */
	public Clazz(Configuration configuration, String qualifiedClassName, String qualifiedMaster, String javaDoc, String fileHeaderText, ThrowingFunction<BasicClazz, HelperTypes> helperFactoryMethod) throws Exception
	{
		super(null, configuration, qualifiedClassName, helperFactoryMethod);
		super.clazzUsingType=this;

		this.qualifiedMaster = qualifiedMaster;
		this.interfaceTypes = new ArrayList<Type>();
		this.baseClazzType = null;
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.fileHeaderText = Objects.requireNonNull(fileHeaderText);

		this.importTypes = new ArrayList<Type>();
		this.modifiers=EnumSet.noneOf(Modifier.class);
	}

	@Override
	public BasicClazz getClazz()
	{
		return this;
	}

	@Override
	public Clazz getGeneratedClazz()
	{
		return this;
	}

	@Override
	public EnumSet<Modifier> getModifiers()
	{
		return modifiers;
	}

	@Override
	public boolean isThisType()
	{
		return true;
	}

	public String getMasterName()
	{
		return NamesUtil.getUnqualifiedName(qualifiedMaster);
	}

	public String getFileHeaderText()
	{
		return fileHeaderText;
	}

	@Override
	public boolean isInImportScope()
	{
		return true;
	}

	/**
     * Nb. Post-constructor for what is inside the class such as methods, members etc. + imports. Calls super class'es initContent internally Both this method and the ancestor class'es {@link ObjectType#initType}
     * methods must be called for the class to be fully initialized and ready for use. Must be called only once.
     *
	 * @param members Member variables for class.
	 * @param properties Property methods for class.
	 * @param methods Non-property methods for class.
	 * @param importTypes Types to be imported for class.
	 * @param chosenComparableMembers Members to be used for compareToOperation
	 * @param modifiers The modifiers to use for code generation (NB: This is not declared modifiers for there are none for a class to be generated)
	 */
	public void initContent(List<Member> members, List<Property> properties, List<Method> methods, List<Type> importTypes, List<Member> chosenComparableMembers, EnumSet<Modifier> modifiers)
	{
		super.initContent(members, methods, EnumSet.noneOf(Modifier.class));

        this.properties=Objects.requireNonNull(properties);
		this.importTypes=Objects.requireNonNull(importTypes);
        this.chosenComparableMembers = Objects.requireNonNull(chosenComparableMembers);

        if (modifiers==null) {
        	if (isAbstract())
    			this.modifiers=EnumSet.of(Modifier.PUBLIC, Modifier.ABSTRACT);
    		else
    			this.modifiers=EnumSet.of(Modifier.PUBLIC, Modifier.FINAL);
        } else this.modifiers=modifiers;
	}

	public String getJavaDoc()
	{
		return javaDoc;
	}

	public boolean isSynchronized()
	{
		assert initialized() : "Class initialization missing";
		return getConfiguration().isSynchronizedAccessEnabled() &&  members.stream().anyMatch(member -> !member.isFinal());
	}

	public boolean isAbstract() {
		assert initialized() : "Class initialization missing";
		return !methods.stream().allMatch(m -> m.implementationInfo!=ImplementationInfo.IMPLEMENTATION_MISSING);
	}

	public List<Property> getPropertyMethods() {
		assert initialized() : "Class initialization missing";
		return properties;
	}

	public List<Member> getChosenComparableMembers()
	{
		assert initialized() : "Class initialization missing";
		return chosenComparableMembers;
	}

	public List<Method> getClaimedImplementationMethods()
	{
		return methods.stream().filter(m -> m.implementationInfo==ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT).collect(Collectors.toList());
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
					 +", base type=" + baseClazzType.toString(level)
					 + System.lineSeparator() + ", interface interfaceTypes=["
					 + interfaceTypes.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator()))+"]"+ System.lineSeparator()+ ", interfaceTypesWithAscendants=["
					 + superTypesWithAscendants.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator())) +"]"+ System.lineSeparator()
 					 + ", importedTypes="+ToStringUtil.toString(importTypes, ", ", level+1)+System.lineSeparator()
					 + ", genericTypeArguments="+ToStringUtil.toString(genericTypeArguments, ", ", level+1)+System.lineSeparator()
					 + ", members="+ToStringUtil.toString(members, System.lineSeparator(), level+1)+System.lineSeparator()
					 + ", properties=" + ToStringUtil.toString(properties,System.lineSeparator(), level+1)+System.lineSeparator()
					 + ", methods="+ToStringUtil.toString(methods,System.lineSeparator(), level+1)+System.lineSeparator()
					 );
		}

		sb.append("]");

		return sb.toString();
	}
}
