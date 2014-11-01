/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.getGenericQualifier;
import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.getPackageFromQualifiedName;
import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.matchingOverloads;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

/**
 * Detailed information about a java "class" such as members and methods. Base class for Clazz which is the class type used for generation of code.
 *
 * @author mmc
 */
public class BasicClazz extends ObjectType implements Definition {

	protected final Configuration configuration;
	protected final String packageName;
	protected final HelperTypes helperTypes;

	protected List<Member> members;
	protected List<Method> methods;
	protected EnumSet<Modifier> declaredModifiers;

	private boolean initializedContent;

	public BasicClazz(BasicClazz optClazzUsingType, Configuration configuration, String qualifiedProtoTypicalTypeName, Function<BasicClazz, HelperTypes> helperFactoryMethod)
	{
		this(optClazzUsingType, configuration, qualifiedProtoTypicalTypeName, helperFactoryMethod, new ArrayList<Member>(), new ArrayList<Method>(), EnumSet.noneOf(Modifier.class));
	}

	private BasicClazz(BasicClazz optClazzUsingType, Configuration configuration, String qualifiedProtoTypicalTypeName, Function<BasicClazz, HelperTypes> helperFactoryMethod, List<Member> members, List<Method> methods, EnumSet<Modifier> declaredModifiers) {
		super(optClazzUsingType, qualifiedProtoTypicalTypeName);
		this.configuration = Objects.requireNonNull(configuration);
		this.packageName = getPackageFromQualifiedName(qualifiedProtoTypicalTypeName);
		this.helperTypes=helperFactoryMethod.apply(this);

		this.methods = methods;
		this.members = members;
		this.declaredModifiers = declaredModifiers;

		initializedContent=false;
	}

	@Override
	public Type copy(BasicClazz clazzUsingType)
	{
		try {
			BasicClazz result = new BasicClazz(clazzUsingType, configuration, qualifiedProtoTypicalTypeName, (c) -> helperTypes, members, methods, declaredModifiers);
			result.baseClazzType=baseClazzType;
			result.interfaceTypes=interfaceTypes;
			result.superTypesWithAscendants=superTypesWithAscendants;
			result.genericTypeArguments=genericTypeArguments;
			result.initializedType=true;
			result.initializedContent=true;
			return result;
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean initialized()
	{
		return initializedType && initializedContent;
	}

	/**
     * Nb. Post-constructor for what is inside the class such as methods, members etc. + imports. Both this method and the super class'es {@link ObjectType#initType}
     * methods must be called for the class to be fully initialized and ready for use. Must be called only once.
     *
	 * @param members Member variables for class.
	 * @param methods Other methods for class.
	 * @param declaredModifiers declared clazz modifiers (empty if a new Clazz)
	 */
	public void initContent(List<Member> members, List<Method> methods, EnumSet<Modifier> declaredModifiers)
	{
		if (initializedContent)
			throw new IllegalStateException("Clazz content already initialized");

        this.members=Objects.requireNonNull(members);
        this.methods=Objects.requireNonNull(methods);
        this.declaredModifiers=Objects.requireNonNull(declaredModifiers);

        initializedContent=true;
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	public HelperTypes getHelperTypes() {
		return helperTypes;
	}

	@Override
	public EnumSet<Modifier> getDeclaredModifiers()
	{
		return declaredModifiers;
	}

	@Override
	public EnumSet<Modifier> getModifiers()
	{
		return EnumSet.of(Modifier.PUBLIC);
	}

	@Override
	public DetailLevel getDetailLevel()
	{
		return DetailLevel.High;
	}

	@Override
	public boolean isThisSuperType()
	{
		return this==clazzUsingType.getBaseClazzType();
	}

	@Override
	public boolean canBeMoreDetailed()
	{
		return false;
	}

	public boolean hasGenericQualifier() {
		return !getGenericQualifierText().isEmpty();
	}

	public String getGenericQualifierText() {
		return getGenericQualifier(qualifiedProtoTypicalTypeName);
	}

	public Member tryGetMember(String name)
	{
		return getMembers().stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
	}

	public Member tryGetMemberIncludingInherited(String name)
	{
		return getMembersIncludingInherited().stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
	}

	public Method tryGetMethod(String name)
	{
		return getMethods().stream().filter(m -> m.getName().equals(name)).findFirst().orElse(null);
	}

	@Override
	public List<Member> getMembers() {
		assert initialized() : "Class initialization missing";
		return members;
	}

	@Override
	public List<Method> getMethods() {
		assert initialized() : "Class initialization missing";
		return methods;
	}

	public List<Member> getMembersIncludingInherited() {
		assert initialized() : "Class initialization missing";
		List<Member> allMembers = new ArrayList<Member>();
		allMembers.addAll(getBaseClazzType().getMembers());
		allMembers.addAll(getMembers());
		return allMembers;
	}

	@Override
	public boolean hasStaticMethod(String overloadName)
	{
		return getMethods().stream().anyMatch(m -> matchingOverloads(m.getOverloadName(), overloadName, false) && m.getModifiers().contains(Modifier.STATIC) && !m.getModifiers().contains(Modifier.PRIVATE));
	}

	@Override
	public boolean hasInstanceMethod(String overloadName)
	{
		return getMethods().stream().anyMatch(m -> matchingOverloads(m.getOverloadName(), overloadName, false) && m.getModifiers().contains(Modifier.PUBLIC) && !m.getModifiers().contains(Modifier.STATIC) && !m.getModifiers().contains(Modifier.PRIVATE));
	}

	@Override
	public boolean hasStaticMember(String name)
	{
		return getMembers().stream().anyMatch(m -> m.getName().equals(name) && m.getModifiers().contains(Modifier.STATIC) && !m.getModifiers().contains(Modifier.PRIVATE));

	}

	@Override
	public boolean hasInstanceMember(String name)
	{
		return getMembers().stream().anyMatch(m -> m.getName().equals(name) && !m.getModifiers().contains(Modifier.STATIC) && !m.getModifiers().contains(Modifier.PRIVATE));
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

		sb.append("BasicClazz [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
		{
			sb.append(", initialized="+initialized()+", qualifiedClassName="+ qualifiedProtoTypicalTypeName);
		}

		// Specific to basic class, most details are only printed as first two levels.
		if (level<=0)
		{
			sb.append(" packageName=" + packageName + System.lineSeparator()
					 +", base type=" + baseClazzType.toString(level+1)
					 + System.lineSeparator() + ", interface interfaceTypes=["
					 + interfaceTypes.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator()))+"]"+ System.lineSeparator()+ ", interfaceTypesWithAscendants=["
					 + superTypesWithAscendants.stream().map(t -> t.toString(level+1)).collect(Collectors.joining(","+System.lineSeparator())) +"]"+ System.lineSeparator()
					 + ", genericTypeArguments="+ToStringUtil.toString(genericTypeArguments, ", ", level+1)+System.lineSeparator()
					 + ", members="+ToStringUtil.toString(members, System.lineSeparator(), level+1)+System.lineSeparator()
					 + ", methods="+ToStringUtil.toString(methods, System.lineSeparator(), level+1)+System.lineSeparator()
					 + ", declaredModifiers="+declaredModifiers
                     );
		}

		sb.append("]");

		return sb.toString();
	}
}