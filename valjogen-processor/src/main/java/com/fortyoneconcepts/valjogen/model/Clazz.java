/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Information about the java "class" that need to be generated including additional information about methods that acts as properties.
 * Together with its supertypes it refers to other model elements like members, properties, methods, types etc.
 *
 * @author mmc
 */
public class Clazz extends BasicClazz implements Model
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
	 */
	public Clazz(Configuration configuration, String qualifiedClassName, String qualifiedMaster, String javaDoc, String fileHeaderText, Function<BasicClazz, HelperTypes> helperFactoryMethod)
	{
		super(null, configuration, qualifiedClassName, helperFactoryMethod);
		super.clazzUsingType=this;

		this.qualifiedMaster = qualifiedMaster;
		this.interfaceTypes = new ArrayList<Type>();
		this.baseClazzType = null;
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.fileHeaderText = Objects.requireNonNull(fileHeaderText);

		this.importTypes = new ArrayList<Type>();
		this.properties = Collections.emptyList();
		this.chosenComparableMembers = Collections.emptyList();
		this.modifiers=EnumSet.noneOf(Modifier.class);
	}

	@Override
	public Type copy(BasicClazz clazzUsingType)
	{
		if (clazzUsingType==this)
			return this;
		else throw new RuntimeException("Copy to new owner not supported for Clazz type");
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
	 * @param annotations Annotations to use for code generation.
	 */
	public void initContent(List<Member> members, List<Property> properties, List<Method> methods, List<Type> importTypes, List<Member> chosenComparableMembers, EnumSet<Modifier> modifiers, List<Annotation> annotations)
	{
		super.initContent(members, methods, EnumSet.noneOf(Modifier.class), annotations);

        this.properties=Objects.requireNonNull(properties);
		this.importTypes=Objects.requireNonNull(importTypes);
        this.chosenComparableMembers = Objects.requireNonNull(chosenComparableMembers);
        this.modifiers = Objects.requireNonNull(modifiers);
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

	public List<Constructor> getClaimedImplementationConstructors()
	{
		return methods.stream().filter(m -> m.implementationInfo==ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT && m.isConstructor() && !m.isStatic()).map(m -> (Constructor)m).collect(Collectors.toList());
	}

	public List<Method> getClaimedImplementationInstanceMethods()
	{
		return methods.stream().filter(m -> m.implementationInfo==ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT && !m.isConstructor() && !m.isStatic()).collect(Collectors.toList());
	}

	public List<Method> getClaimedImplementationClassMethods()
	{
		return methods.stream().filter(m -> m.implementationInfo==ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT && !m.isConstructor() && m.isStatic()).collect(Collectors.toList());
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
	protected void printExtraTop(IndentedPrintWriter writer, int detailLevel)
	{
		writer.print(", modifiers="+modifiers);
	}

	@Override
	protected void printExtraBottom(IndentedPrintWriter writer, int detailLevel)
	{
		if (properties.size()>0) {
		  writer.print("properties= [");
		  properties.stream().forEach(p -> p.print(writer, detailLevel+1));
		  writer.println("]");
		}

		if (importTypes.size()>0) {
		  writer.println("importTypes=["+importTypes.stream().map(t -> t.getQualifiedName()).collect(Collectors.joining(", "))+"]");
		}

		if (chosenComparableMembers.size()>0) {
		  writer.println("chosenComparableMembers=["+chosenComparableMembers.stream().map(m -> m.getName()).collect(Collectors.joining(", "))+"]");
		}
	}
}
