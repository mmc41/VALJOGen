package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.getGenericQualifier;
import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.getPackageFromQualifiedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.ThrowingFunction;
import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

/**
 * Detailed information about a java "class" such as members, properties and methods. Base class for Clazz which is the class type used for generation of code.
 *
 * @author mmc
 */
public class BasicClazz extends ObjectType {

	protected final Configuration configuration;
	protected final String packageName;
	protected final HelperTypes helperTypes;
	protected List<Member> members;
	protected List<Property> properties;
	protected List<Method> methods;

	private boolean initializedContent;

	public BasicClazz(Configuration configuration, String qualifiedProtoTypicalTypeName, ThrowingFunction<BasicClazz, HelperTypes> helperFactoryMethod) throws Exception {
		super(qualifiedProtoTypicalTypeName);
		super.clazzUsingType=this;
		this.configuration = Objects.requireNonNull(configuration);
		this.packageName = getPackageFromQualifiedName(qualifiedProtoTypicalTypeName);
		this.helperTypes=helperFactoryMethod.apply(this);

		this.properties = new ArrayList<Property>();
		this.methods = new ArrayList<Method>();
		this.members = new ArrayList<Member>();

		initializedContent=false;
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
	 * @param properties Property methods for class.
	 * @param nonPropertyMethods Other methods for class.
	 */
	public void initContent(List<Member> members, List<Property> properties, List<Method> nonPropertyMethods)
	{
		if (initializedContent)
			throw new IllegalStateException("Clazz content already initialized");

        this.members=Objects.requireNonNull(members);
        this.properties=Objects.requireNonNull(properties);
        this.methods=Objects.requireNonNull(nonPropertyMethods);

        initializedContent=true;
	}

	@Override
	public BasicClazz getClazz()
	{
		return this;
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public HelperTypes getHelperTypes() {
		return helperTypes;
	}

	public boolean hasGenericQualifier() {
		return !getGenericQualifierText().isEmpty();
	}

	public String getGenericQualifierText() {
		return getGenericQualifier(qualifiedProtoTypicalTypeName);
	}

	public boolean isFinal() {
		return !isAbstract() && getConfiguration().isFinalClassEnabled();
	}

	public boolean isAbstract() {
		assert initialized() : "Class initialization missing";
		return !methods.stream().allMatch(m -> m.implementationInfo!=ImplementationInfo.IMPLEMENTATION_MISSING);
	}

	public boolean hasPrimitiveMembers() {
		assert initialized() : "Class initialization missing";
		return members.stream().anyMatch(m -> m.getType().isPrimitive());
	}

	public boolean hasArrayMembers() {
		assert initialized() : "Class initialization missing";
		return members.stream().anyMatch(m -> m.getType().isArray());
	}

	public List<Member> getMembers() {
		assert initialized() : "Class initialization missing";
		return members;
	}

	public boolean hasAnyMembers() {
		assert initialized() : "Class initialization missing";
		return !members.isEmpty();
	}

	public List<Property> getPropertyMethods() {
		assert initialized() : "Class initialization missing";
		return properties;
	}

	public List<Method> getMethods() {
		assert initialized() : "Class initialization missing";
		return methods;
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