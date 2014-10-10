/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.beans.Introspector;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Stream.*;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.*;
import javax.tools.Diagnostic.Kind;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.util.*;

/***
 * This class is responsible for transforming data in the javax.lang.model.* format to our own valjogen models.
 *
 * The javax.lang.model.* lacks detailed documentation so some points is included here:
 * - Elements are about the static structure of the program, ie packages, classes, methods and variables (similar to what is seen in a package explorer in an IDE).
 * - Types are about the statically defined type constraints of the program, i.e. types, generic type parameters, generic type wildcards (Everything that is part of Java's type declarations before type erasure).
 * - Mirror objects is where you can see the reflection of the object, thus seperating queries from the internal structure. This allows reflectiong on stuff that has not been loaded.
 *
 * @author mmc
 */
public final class ModelBuilder
{
	// private final static Logger LOGGER = Logger.getLogger(ModelBuilder.class.getName());

	/**
	 * Methods that are not called dynamically but (currently) implemented in a special way by the templates.
	 */
	private Set<String> specialMethods = new HashSet<String>(Arrays.asList("valueOf()", "this()"));

	/**
	 * Methods that may or may not be implemented (no warnings issued if supplied but not implemented).
	 */
	private Set<String> optionalMethods = new HashSet<String>(Arrays.asList("compareTo(T)"));

	private final TypeBuilder typeBuilder;

	private final Types types;
	private final Elements elements;
	private final DiagnosticMessageConsumer errorConsumer;
	private final TypeElement masterInterfaceElement;
	private final Configuration configuration;
	private final ResourceLoader resourceLoader;
	private final STTemplates templates;

	/**
	 * Contains various data that streams need to manipulate and this needs to be accessed by reference.
	 *
	 * @author mmc
	 */
	private class StatusHolder
	{
		public boolean encountedSynthesisedMembers = false;
	}

	/**
	 * Create an instance of this builder that can build the specified class and all dependencies.
	 *
	 * @param types Types helper from javax.lang.model
	 * @param elements Elements helper from jacax.lang.model
	 * @param errorConsumer Where to send errors.
	 * @param masterInterfaceElement The interface that has been selected for code generation (by an annotation).
	 * @param configuration Descripes the user-selected details about what should be generated (combination of annotation(s) and annotation processor setup).
	 * @param resourceLoader What to call to get resource files
	 * @param templates StringTemplate templates holder used to reflect on what methods are supplied.
	 */
	public ModelBuilder(Types types, Elements elements, DiagnosticMessageConsumer errorConsumer, TypeElement masterInterfaceElement, Configuration configuration, ResourceLoader resourceLoader, STTemplates templates)
	{
      this.types=types;
	  this.elements=elements;
	  this.errorConsumer=errorConsumer;
	  this.masterInterfaceElement=masterInterfaceElement;
	  this.configuration=configuration;
	  this.resourceLoader=resourceLoader;
	  this.templates=templates;
	  this.typeBuilder=new TypeBuilder(types, elements, errorConsumer, masterInterfaceElement);
	}

	private static class ExecutableElementAndDeclaredTypePair
	{
		public final DeclaredType interfaceDecl;
		public final ExecutableElement executableElement;

		public ExecutableElementAndDeclaredTypePair(DeclaredType interfaceDecl, ExecutableElement executableElement)
		{
			this.interfaceDecl=interfaceDecl;
			this.executableElement=executableElement;
		}
	}

	/**
    * Create a Clazz model instance representing a class to be generated along with all its dependent model instances by inspecting
    * javax.lang.model metadata and the configuration provided by annotation(s) read by annotation processor.
	*
	* @return A initialized Clazz which is a model for what our generated code should look like.
	*
	* @throws Exception if a fatal error has occured.
	*/
	public Clazz buildNewCLazz() throws Exception
	{
		// Step 1 - Create clazz:
		DeclaredType masterInterfaceDecl = (DeclaredType)masterInterfaceElement.asType();

		PackageElement sourcePackageElement = elements.getPackageOf(masterInterfaceElement);

		String sourceInterfacePackageName = sourcePackageElement.isUnnamed() ? "" : sourcePackageElement.toString();
		String className = createQualifiedClassName(masterInterfaceElement.asType().toString(), sourceInterfacePackageName);
		String classPackage = NamesUtil.getPackageFromQualifiedName(className);

		String classJavaDoc = elements.getDocComment(masterInterfaceElement);
		if (classJavaDoc==null) // hmmm - seems to be null always (api not working?)
			classJavaDoc="";

		String headerFileName = configuration.getHeaderFileName();
		String headerText = "";
		if (headerFileName!=null)
		{
			headerText=resourceLoader.getResourceAsText(headerFileName);
		}

		Clazz clazz = new Clazz(configuration, className, masterInterfaceElement.getQualifiedName().toString(), classJavaDoc, headerText, (c) -> typeBuilder.createHelperTypes(c));

		DeclaredType baseClazzDeclaredMirrorType = createBaseClazzDeclaredType(classPackage);
		if (baseClazzDeclaredMirrorType==null)
			return null;

		List<DeclaredType> allBaseClassDeclaredMirrorTypes =  typeBuilder.getSuperTypesWithAscendents(baseClazzDeclaredMirrorType).collect(Collectors.toList());

		String[] ekstraInterfaceNames = configuration.getExtraInterfaces();

		List<DeclaredType> interfaceDeclaredMirrorTypes = typeBuilder.createInterfaceDeclaredTypes(masterInterfaceDecl, ekstraInterfaceNames, classPackage);
		List<DeclaredType> allInterfaceDeclaredMirrorTypes = interfaceDeclaredMirrorTypes.stream().flatMap(ie -> typeBuilder.getDeclaredInterfacesWithAscendents(ie)).collect(Collectors.toList());

        // Step 2 - Init type part of clzzz:
		List<? extends TypeMirror> typeArgs = masterInterfaceDecl.getTypeArguments();

	    List<Type> typeArgTypes = typeArgs.stream().map(t -> typeBuilder.createType(clazz, t, DetailLevel.Low)).collect(Collectors.toList());

		// List<GenericParameter> genericParameters = masterInterfaceElement.getTypeParameters().stream().map(p -> createGenericParameter(clazz, allTypesByPrototypicalFullName, types, p)).collect(Collectors.toList());

		Type baseClazzType = typeBuilder.createType(clazz, baseClazzDeclaredMirrorType, DetailLevel.Low);

		List<Type> interfaceTypes = interfaceDeclaredMirrorTypes.stream().map(ie -> typeBuilder.createType(clazz, ie, DetailLevel.Low)).collect(Collectors.toList());
		Set<Type> interfaceTypesWithAscendants = allInterfaceDeclaredMirrorTypes.stream().map(ie -> typeBuilder.createType(clazz, ie, DetailLevel.Low)).collect(Collectors.toSet());

		clazz.initType(baseClazzType, interfaceTypes, interfaceTypesWithAscendants, typeArgTypes);

		// Step 3 - Init content part of clazz:
		Map<String, Member> membersByName = new LinkedHashMap<String, Member>();
		List<Method> nonPropertyMethods = new ArrayList<Method>();
		List<Property> propertyMethods= new ArrayList<Property>();

		final StatusHolder statusHolder = new StatusHolder();

		Set<String> implementedMethodNames = templates.getTemplateMethodNames();

				//configuration.getImplementedMethodNames();

		// Collect all members, property methods and non-property methods from interfaces paired with the interface they belong to:
		Stream<ExecutableElementAndDeclaredTypePair> executableElementsFromInterfaces = allInterfaceDeclaredMirrorTypes.stream().flatMap(i -> toExecutableElementAndDeclaredTypePair(i, i.asElement().getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD).map(m -> (ExecutableElement)m).filter(m -> {
			Set<Modifier> modifiers = m.getModifiers();
			return !modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.FINAL);
		})));

		Stream<ExecutableElementAndDeclaredTypePair> executableElementsFromBaseClasses = allBaseClassDeclaredMirrorTypes.stream().flatMap(b -> toExecutableElementAndDeclaredTypePair(b, b.asElement().getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD).map(m -> (ExecutableElement)m).filter(m -> {
			Set<Modifier> modifiers = m.getModifiers();
			return !modifiers.contains(Modifier.STATIC) && !modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.FINAL);
		})));

		// Nb. Stream.forEach has side-effects so is not thread-safe and will not work with parallel streams - but do not need to anyway.
		// Currently it is assmued that all methods from base classes are implemented already - this does no take abstract base classes into account.

		// TODO: Support abstract base classes - find out which methods are actually implemented instead of assuming they all are.

		executableElementsFromInterfaces.forEach(e -> processMethod(clazz, membersByName, nonPropertyMethods, propertyMethods, statusHolder, e.executableElement, e.interfaceDecl, false));
		executableElementsFromBaseClasses.forEach(e -> processMethod(clazz, membersByName, nonPropertyMethods, propertyMethods, statusHolder, e.executableElement, e.interfaceDecl, true));

		if (statusHolder.encountedSynthesisedMembers && configuration.isWarningAboutSynthesisedNamesEnabled())
			errorConsumer.message(masterInterfaceElement, Kind.WARNING, String.format(ProcessorMessages.ParameterNamesUnavailable, masterInterfaceElement.toString()));

		List<Type> importTypes = createImportTypes(clazz, baseClazzDeclaredMirrorType, interfaceDeclaredMirrorTypes);

		List<Member> members = new ArrayList<Member>(membersByName.values());
		List<Member> selectedComparableMembers = clazz.isComparable() ? getSelectedComparableMembers(membersByName, members) : Collections.emptyList();

		if (clazz.isSerializable()) {
			nonPropertyMethods.addAll(createMagicSerializationMethods(clazz));
		}

		claimAndVerifyMethods(nonPropertyMethods, implementedMethodNames, propertyMethods);

		clazz.initContent(members, propertyMethods, nonPropertyMethods, filterImportTypes(clazz, importTypes), selectedComparableMembers);

		return clazz;
	}

	/*
	*
    * Create a basic Clazz model instance for an existing class along with all its dependent model instances by inspecting
    * javax.lang.model metadata.
	*
	* return A initialized basic Clazz for an existing.
	*
	* hrows Exception if a fatal error has occured.
	*/
    // TODO make:


	private void claimAndVerifyMethods(List<Method> nonPropertyMethods, Set<String> implementedMethodNames, List<Property> propertyMethods) throws Exception
	{
		Set<String> unusedMethodNames = new HashSet<String>(implementedMethodNames);

		for (Method method : nonPropertyMethods)
		{
			String name = method.getOverloadName();
			for (String claimedImplementedName : implementedMethodNames)
			{
				if (name.equals(claimedImplementedName)) {
					unusedMethodNames.remove(claimedImplementedName);

					method.setImplementationInfo(ImplementationInfo.IMPLEMENTATION_CLAIMED_BY_GENERATED_OBJECT);
					break;
				}
			}
		}

		for (String name : unusedMethodNames) {
		  boolean optional = (optionalMethods.contains(name));
		  boolean special = (specialMethods.contains(name));
		  if (!optional && !special)
		    errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.UNKNOWN_METHOD, name));
		}

		for (Method method : propertyMethods)
		{
			method.setImplementationInfo(ImplementationInfo.IMPLEMENTATION_CLAIMED_BY_GENERATED_OBJECT);
		}
	}

	private List<Method> createMagicSerializationMethods(BasicClazz clazz) throws Exception
	{
		List<Method> newMethods = new ArrayList<>();

		Type noType = clazz.getHelperTypes().getNoType();

		TypeMirror ioExceptionMirrorType = typeBuilder.createTypeFromString("java.io.IOException");
		TypeMirror objectStreamExceptionMirrorType = typeBuilder.createTypeFromString("java.io.ObjectStreamException");
		TypeMirror classNotFoundExceptionMirrorType = typeBuilder.createTypeFromString("java.lang.ClassNotFoundException");

		ObjectType inputStreamType = clazz.getHelperTypes().getInputStreamType();
		ObjectType objectOutputStreamType = clazz.getHelperTypes().getOutputStreamType();

		// Add : private Object readResolve() throws ObjectStreamException :
		Method readResolve = new Method(clazz, AccessLevel.PRIVATE, noType, "readResolve", clazz.getHelperTypes().getJavaLangObjectType(), Collections.emptyList(), Collections.singletonList((ObjectType)typeBuilder.createType(clazz, objectStreamExceptionMirrorType, DetailLevel.Low)), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		newMethods.add(readResolve);

		// Add private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException :
		List<Parameter> readObjectParameters = Collections.singletonList(new Parameter(clazz, inputStreamType, inputStreamType, "in"));
		Method readObject = new Method(clazz, AccessLevel.PRIVATE, noType, "readObject", clazz.getHelperTypes().getVoidType(), readObjectParameters, Arrays.asList(new ObjectType[] { (ObjectType)typeBuilder.createType(clazz, ioExceptionMirrorType, DetailLevel.Low), (ObjectType)typeBuilder.createType(clazz, classNotFoundExceptionMirrorType, DetailLevel.Low) }), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		newMethods.add(readObject);

		// Add private void readObjectNoData() throws InvalidObjectException
		Method readObjectNoData = new Method(clazz, AccessLevel.PRIVATE, noType, "readObjectNoData", clazz.getHelperTypes().getVoidType(), Collections.emptyList(), Collections.singletonList((ObjectType)typeBuilder.createType(clazz, objectStreamExceptionMirrorType, DetailLevel.Low)), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		newMethods.add(readObjectNoData);

		// Add : private void writeObject (ObjectOutputStream out) throws IOException :
		List<Parameter> writeObjectParameters = Collections.singletonList(new Parameter(clazz, objectOutputStreamType, objectOutputStreamType, "out"));
		Method writeObject = new Method(clazz, AccessLevel.PRIVATE, noType, "writeObject", clazz.getHelperTypes().getVoidType(), writeObjectParameters, Collections.singletonList((ObjectType)typeBuilder.createType(clazz, ioExceptionMirrorType, DetailLevel.Low)), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		newMethods.add(writeObject);

		// Add : private Object writeReplace() throws ObjectStreamException :
		Method writeReplace = new Method(clazz, AccessLevel.PRIVATE, noType, "writeReplace", clazz.getHelperTypes().getJavaLangObjectType(), Collections.emptyList(), Collections.singletonList((ObjectType)typeBuilder.createType(clazz, objectStreamExceptionMirrorType, DetailLevel.Low)), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		newMethods.add(writeReplace);

		return newMethods;
	}

	private List<Member> getSelectedComparableMembers(Map<String, Member> membersByName, List<Member> members) throws Exception
	{
		List<Member> comparableMembers;

		String[] comparableMemberNames = configuration.getComparableMembers();
		if (comparableMemberNames.length==0)
		{
			comparableMembers=members.stream().filter(m -> m.getType().isComparable()).collect(Collectors.toList());
			if (comparableMembers.size()!=members.size())
			{
				errorConsumer.message(masterInterfaceElement, Kind.WARNING, String.format(ProcessorMessages.NotAllMembersAreComparable, masterInterfaceElement.toString()));
			}
		} else {
			comparableMembers=new ArrayList<Member>();

			for (String comparableMemberName : comparableMemberNames)
			{
				Member member = membersByName.get(comparableMemberName);
				if (member!=null) {
					comparableMembers.add(member);
					if (!member.getType().isComparable()) {
						errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.MemberNotComparable, comparableMemberName));
					}
				} else {
					errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.MemberNotFound, comparableMemberName));
				}
			}
		}

		return comparableMembers;
	}

	private Stream<ExecutableElementAndDeclaredTypePair> toExecutableElementAndDeclaredTypePair(DeclaredType interfaceOrClasMirrorType, Stream<ExecutableElement> elements)
	{
		return elements.map(e -> new ExecutableElementAndDeclaredTypePair(interfaceOrClasMirrorType, e));
	}

	private void processMethod(BasicClazz clazz, Map<String, Member> membersByName, List<Method> nonPropertyMethods, List<Property> propertyMethods, final StatusHolder statusHolder, ExecutableElement m, DeclaredType interfaceOrClassMirrorType, boolean implementedAlready)
	{
		try {
			String javaDoc = elements.getDocComment(m);
			if (javaDoc==null) // hmmm - seems to be null always (api not working?)
				javaDoc="";

			ExecutableType executableMethodMirrorType = (ExecutableType)types.asMemberOf(interfaceOrClassMirrorType, m);

			Type declaringType = typeBuilder.createType(clazz, interfaceOrClassMirrorType, DetailLevel.Low);

			String methodName = m.getSimpleName().toString();

			TypeMirror returnTypeMirror = executableMethodMirrorType.getReturnType();
			Type returnType = typeBuilder.createType(clazz, returnTypeMirror, DetailLevel.Low);

			List<? extends VariableElement> params =  m.getParameters();
			List<? extends TypeMirror> paramTypes = executableMethodMirrorType.getParameterTypes();

			if (params.size()!=paramTypes.size())
				throw new Exception("Internal error - Numbers of method parameters "+params.size()+" and method parameter types "+paramTypes.size()+" does not match");

			List<? extends TypeMirror> thrownTypeMirrors = executableMethodMirrorType.getThrownTypes();
			List<Type> thrownTypes = thrownTypeMirrors.stream().map(ie -> typeBuilder.createType(clazz, ie, DetailLevel.Low)).collect(Collectors.toList());

			List<Parameter> parameters = new ArrayList<Parameter>();
			for (int i=0; i<params.size(); ++i)
			{
				Parameter param = createParameter(clazz, params.get(i), paramTypes.get(i));
				parameters.add(param);
			}

			boolean validProperty = false;
		    if (!m.isDefault() && !implementedAlready)
			{
				PropertyKind propertyKind = null;
			    if (NamesUtil.isGetterMethod(methodName, configuration.getGetterPrefixes()))
			    	propertyKind=PropertyKind.GETTER;
			    else if (NamesUtil.isSetterMethod(methodName, configuration.getSetterPrefixes()))
			    	propertyKind=PropertyKind.SETTER;

				if (propertyKind!=null) {
					Member propertyMember = createPropertyMemberIfValidProperty(clazz, interfaceOrClassMirrorType, returnTypeMirror, params, paramTypes, m, propertyKind);

					if (propertyMember!=null) {
			            final Member existingMember = membersByName.putIfAbsent(propertyMember.getName(), propertyMember);
			          	if (existingMember!=null) {
			          	   if (!existingMember.getType().equals(propertyMember.getType())) {
			          		 if (!configuration.isMalformedPropertiesIgnored()) {
			          			  String propertyNames = concat(existingMember.getPropertyMethods().stream().map(p -> p.getName()), of(propertyMember.getName())).collect(Collectors.joining(", "));
			     				  errorConsumer.message(m, Kind.ERROR, String.format(ProcessorMessages.InconsistentProperty, propertyNames ));
			          		 }
			          	   }

			          	   propertyMember = existingMember;
			          	}

			          	Property property = createValidatedProperty(clazz, statusHolder, declaringType, m, returnType, parameters, thrownTypes, javaDoc, propertyKind, propertyMember, ImplementationInfo.IMPLEMENTATION_MISSING);

			          	propertyMember.addPropertyMethod(property);
			          	propertyMethods.add(property);
			          	validProperty=true;
					}
				}
			}

			if (!validProperty)
			{
			    ImplementationInfo implementationInfo;
				if (implementedAlready)
					implementationInfo=ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_BASE_OBJECT;
				else if (m.isDefault())
					implementationInfo=ImplementationInfo.IMPLEMENTATION_DEFAULT_PROVIDED;
				else implementationInfo=ImplementationInfo.IMPLEMENTATION_MISSING;

				nonPropertyMethods.add(new Method(clazz, AccessLevel.PUBLIC, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, implementationInfo));
			}
		} catch (Exception e)
		{
			String location = m.getEnclosingElement().toString()+"."+m.getSimpleName().toString();
			throw new RuntimeException("Failure during processing of "+location+" due to "+e.getMessage(), e);
		}
	}

	private Parameter createParameter(BasicClazz clazz, VariableElement param, TypeMirror paramType)
	{
		String name = param.getSimpleName().toString();
		return new Parameter(clazz, typeBuilder.createType(clazz, paramType, DetailLevel.Low), typeBuilder.createType(clazz, param.asType(), DetailLevel.Low), name);
	}

	private DeclaredType createBaseClazzDeclaredType(String clazzPackage) throws Exception
	{
		String baseClazzName = configuration.getBaseClazzName();
		if (baseClazzName==null || baseClazzName.isEmpty())
			baseClazzName=ConfigurationDefaults.RootObject;

		return typeBuilder.createDeclaredTypeFromString(baseClazzName, clazzPackage);
	}

	private List<Type> createImportTypes(BasicClazz clazz, DeclaredType baseClazzDeclaredType, List<DeclaredType> implementedDecalredInterfaceTypes) throws Exception
	{
		List<Type> importTypes = new ArrayList<Type>();
		for (DeclaredType implementedInterfaceDeclaredType : implementedDecalredInterfaceTypes)
		  importTypes.add(typeBuilder.createType(clazz, implementedInterfaceDeclaredType, DetailLevel.Low));

		importTypes.add(typeBuilder.createType(clazz, baseClazzDeclaredType, DetailLevel.Low));

		for (String importName : configuration.getImportClasses())
		{
			TypeElement importElement = elements.getTypeElement(importName);
			if (importElement==null) {
				errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ImportTypeNotFound, importName));
			} else {
			   Type importElementType = typeBuilder.createType(clazz, importElement.asType(), DetailLevel.Low);
			   importTypes.add(importElementType);
			}
		}
		return importTypes;
	}

	private Property createValidatedProperty(BasicClazz clazz, StatusHolder statusHolder, Type declaringType, ExecutableElement m, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, PropertyKind propertyKind, Member propertyMember, ImplementationInfo implementationInfo)
	{
		Property property;

		String propertyName = m.getSimpleName().toString();

      	Type overriddenReturnType = (configuration.isThisAsImmutableSetterReturnTypeEnabled() && propertyKind==PropertyKind.SETTER && !returnType.isVoid()) ? clazz : returnType;

		if (parameters.size()==0) {
			property=new Property(clazz, AccessLevel.PUBLIC, declaringType, propertyName, returnType, overriddenReturnType, thrownTypes, propertyMember, propertyKind, javaDoc, implementationInfo);
		} else if (parameters.size()==1) {
			Parameter parameter = parameters.get(0);

			// Parameter names may be syntesised so we can fall back on using member
			// name as parameter name. Note if this happen so we can issue a warning later.
			if (parameter.getName().matches("arg\\d+")) { // Synthesised check (not bullet-proof):
				statusHolder.encountedSynthesisedMembers=true;
				parameter=parameter.setName(propertyMember.getName());
			}

			property = new Property(clazz, AccessLevel.PUBLIC, declaringType, propertyName, returnType, overriddenReturnType, thrownTypes, propertyMember, propertyKind, javaDoc, implementationInfo, parameter);
		} else throw new RuntimeException("Unexpected number of formal parameters for property "+m.toString()); // Should not happen for a valid propety unless validation above has a programming error.

		return property;
	}

	private List<Type> filterImportTypes(BasicClazz clazz, List<Type> importTypes)
	{
		List<Type> result = new ArrayList<Type>();

		for (Type type : importTypes)
		{
			if (type.getPackageName().equals("java.lang"))
				continue;

			if (type.getPackageName().equals(clazz.getPackageName()))
			    continue;

			if (result.stream().anyMatch(existingType -> existingType.getQualifiedName().equals(type.getQualifiedName())))
			   continue;

			result.add(type);
		}

		return result;
	}

	private String createQualifiedClassName(String qualifedInterfaceName, String sourcePackageName)
	{
		String className = configuration.getName();
		if (className==null || className.isEmpty())
			className = NamesUtil.createNewClassNameFromInterfaceName(qualifedInterfaceName);

		if (!NamesUtil.isQualified(className))
		{
			String packageName = configuration.getPackage();
			if (packageName==null)
				packageName=sourcePackageName;

			if (!packageName.isEmpty())
				className=packageName+"."+className;
		}

		return className;
	}

	private Member createPropertyMemberIfValidProperty(BasicClazz clazz, DeclaredType interfaceOrClassMirrorType,
			                                           TypeMirror returnTypeMirror, List<? extends VariableElement> setterParams, List<? extends TypeMirror> setterParamTypes,
			                                           ExecutableElement methodElement, PropertyKind kind) throws Exception
	{
		TypeMirror propertyTypeMirror;

		if (kind==PropertyKind.GETTER) {
			if (setterParams.size()!=0) {
				if (!configuration.isMalformedPropertiesIgnored())
				  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedGetter, methodElement.toString()));
				return null;
			}

			propertyTypeMirror = returnTypeMirror;
			return new Member(clazz, typeBuilder.createType(clazz, propertyTypeMirror, DetailLevel.Low), syntesisePropertyMemberName(configuration.getGetterPrefixes(), methodElement));
		} else if (kind==PropertyKind.SETTER) {
			if (setterParams.size()!=1) {
				if (!configuration.isMalformedPropertiesIgnored())
  				  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedSetter, methodElement.toString()));
				return null;
			}

			String returnTypeName = returnTypeMirror.toString();
			// Type returnType = typeBuilder.createType(clazz, allTypesByPrototypicalFullName, types, returnTypeMirror);

			String declaredInterfaceTypeName = interfaceOrClassMirrorType.toString();
			if (!returnTypeName.equals("void") && !returnTypeName.equals(declaredInterfaceTypeName) && !returnTypeName.equals(clazz.getQualifiedName())) {
				if (!configuration.isMalformedPropertiesIgnored())
					  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedSetter, methodElement.toString()));
				return null;
			}

			propertyTypeMirror=setterParamTypes.get(0);
			return new Member(clazz, typeBuilder.createType(clazz, propertyTypeMirror, DetailLevel.Low), syntesisePropertyMemberName(configuration.getSetterPrefixes(), methodElement));
		} else {
			return null; // Not a property.
		}
	}

	private String syntesisePropertyMemberName(String[] propertyPrefixes, ExecutableElement method)
	{
		String name = method.getSimpleName().toString();

		int i=0;
		while (i<propertyPrefixes.length)
		{
			String prefix=propertyPrefixes[i++];
			int skip=prefix.length();
			if (name.startsWith(prefix) && name.length()>skip)
			{
				name=name.substring(skip);
				name=Introspector.decapitalize(name);
				name=NamesUtil.makeSafeJavaIdentifier(name);
				return name;
			}
		}

		return name;
	}

	/*
	public static boolean isCallableConstructor(ExecutableElement constructor) {
	 if (constructor.getModifiers().contains(Modifier.PRIVATE)) {
	   return false;
	 }

	 TypeElement type = (TypeElement) constructor.getEnclosingElement();
	 return type.getEnclosingElement().getKind() == ElementKind.PACKAGE || type.getModifiers().contains(Modifier.STATIC);
	}*/
}
