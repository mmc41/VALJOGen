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
import javax.lang.model.type.ArrayType;
import javax.lang.model.util.*;
import javax.tools.Diagnostic.Kind;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.NoType;
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
public final class ClazzFactory
{
	// private final static Logger LOGGER = Logger.getLogger(ClazzFactory.class.getName());

	private static ClazzFactory instance = null;

	public static synchronized ClazzFactory getInstance() {
	    if(instance == null) {
	        instance = new ClazzFactory();
	    }
	    return instance;
	}

	/**
	 * Contains various data that streams need to manipulate and this needs to be accessed by reference.
	 *
	 * @author mmc
	 */
	private class StatusHolder
	{
		public boolean encountedSynthesisedMembers = false;
	}

	private ClazzFactory() {}

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
    * Create a Clazz model instance along with all its dependen model instancess by inspecting
    * javax.model metadata and the configuration provided by annotation(s) read by annotation processor.
    *
	* @param types Utility instance provided by javax.lang.model framework.
	* @param elements Utility instance provided by javax.lang.model framework.
	* @param masterInterfaceElement The interface that has been selected for code generation (by an annotation).
	* @param configuration Descripes the user-selected details about what should be generated (combination of annotation(s) and annotation processor setup).
	* @param errorConsumer Where to report errors and warning
	* @param resourceLoader What to call to get resource files
	*
	* @return A initialized Clazz which is a model for what our generated code should look like.
	*
	* @throws Exception if a fatal error has occured.
	*/
	public Clazz createClazz(Types types, Elements elements, TypeElement masterInterfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer, ResourceLoader resourceLoader) throws Exception
	{
		// Step 1 - Create clazz:
		DeclaredType masterInterfaceDecl = (DeclaredType)masterInterfaceElement.asType();

		Map<String,Type> allTypesByPrototypicalFullName = new HashMap<String, Type>();

		PackageElement sourcePackageElement = elements.getPackageOf(masterInterfaceElement);

		String sourceInterfacePackageName = sourcePackageElement.isUnnamed() ? "" : sourcePackageElement.toString();
		String className = createQualifiedClassName(configuration, masterInterfaceElement.asType().toString(), sourceInterfacePackageName);
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

		Clazz clazz = new Clazz(configuration, className, masterInterfaceElement.getQualifiedName().toString(), classJavaDoc, headerText);

		DeclaredType baseClazzDeclaredMirrorType = createBaseClazzDeclaredType(elements, types, masterInterfaceElement, configuration, errorConsumer, classPackage);
		if (baseClazzDeclaredMirrorType==null)
			return null;

		List<DeclaredType> allBaseClassDeclaredMirrorTypes =  getSuperTypesWithAscendents(types, baseClazzDeclaredMirrorType).collect(Collectors.toList());

		String[] ekstraInterfaceNames = configuration.getExtraInterfaces();

		List<DeclaredType> interfaceDeclaredMirrorTypes = createInterfaceDeclaredTypes(elements, types, masterInterfaceElement, masterInterfaceDecl, ekstraInterfaceNames, errorConsumer, classPackage);
		List<DeclaredType> allInterfaceDeclaredMirrorTypes = interfaceDeclaredMirrorTypes.stream().flatMap(ie -> getDeclaredInterfacesWithAscendents(types, ie)).collect(Collectors.toList());

        // Step 2 - Init type part of clzzz:
		List<? extends TypeMirror> typeArgs = masterInterfaceDecl.getTypeArguments();

	    List<Type> typeArgTypes = typeArgs.stream().map(t -> createType(clazz, allTypesByPrototypicalFullName, types, t)).collect(Collectors.toList());

		// List<GenericParameter> genericParameters = masterInterfaceElement.getTypeParameters().stream().map(p -> createGenericParameter(clazz, allTypesByPrototypicalFullName, types, p)).collect(Collectors.toList());

		Type baseClazzType = createType(clazz, allTypesByPrototypicalFullName, types, baseClazzDeclaredMirrorType);

		List<Type> interfaceTypes = interfaceDeclaredMirrorTypes.stream().map(ie -> createType(clazz, allTypesByPrototypicalFullName, types, ie)).collect(Collectors.toList());
		Set<Type> interfaceTypesWithAscendants = allInterfaceDeclaredMirrorTypes.stream().map(ie -> createType(clazz, allTypesByPrototypicalFullName, types, ie)).collect(Collectors.toSet());

		clazz.initType(baseClazzType, interfaceTypes, interfaceTypesWithAscendants, typeArgTypes);

		// Step 3 - Init content part of clazz:
		Map<String, Member> membersByName = new LinkedHashMap<String, Member>();
		List<Method> nonPropertyMethods = new ArrayList<Method>();
		List<Property> propertyMethods= new ArrayList<Property>();

		final StatusHolder statusHolder = new StatusHolder();

		Set<String> implementedMethodNames = configuration.getImplementedMethodNames();

		// TODO: Consider adding type argument signatures to support overloading.
		if (clazz.isComparable() && configuration.isComparableEnabled())
			implementedMethodNames.add("compareTo");
		if (configuration.isHashEnabled())
			implementedMethodNames.add("hashCode");
		if (configuration.isEqualsEnabled())
			implementedMethodNames.add("equals");
		if (configuration.isToStringEnabled())
			implementedMethodNames.add("toString");

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
		// TODO: Support abstract base classes - find out which methods are actually implemented instead of assuming.

		executableElementsFromInterfaces.forEach(e -> processMethod(types, elements, masterInterfaceElement, configuration, errorConsumer, allTypesByPrototypicalFullName, clazz, membersByName, nonPropertyMethods, propertyMethods, statusHolder, e.executableElement, e.interfaceDecl, implementedMethodNames, false));
		executableElementsFromBaseClasses.forEach(e -> processMethod(types, elements, masterInterfaceElement, configuration, errorConsumer, allTypesByPrototypicalFullName, clazz, membersByName, nonPropertyMethods, propertyMethods, statusHolder, e.executableElement, e.interfaceDecl, implementedMethodNames, true));

		if (statusHolder.encountedSynthesisedMembers)
			errorConsumer.message(masterInterfaceElement, Kind.WARNING, String.format(ProcessorMessages.ParameterNamesUnavailable, masterInterfaceElement.toString()));

		List<Type> importTypes = createImportTypes(clazz, allTypesByPrototypicalFullName, types, elements, masterInterfaceElement, configuration, baseClazzDeclaredMirrorType, interfaceDeclaredMirrorTypes, errorConsumer);

		List<Member> members = new ArrayList<Member>(membersByName.values());
		List<Member> selectedComparableMembers = (clazz.isComparable() && configuration.isComparableEnabled()) ? getSelectedComparableMembers(masterInterfaceElement, configuration, errorConsumer, membersByName, members) : Collections.emptyList();

		if (clazz.isSerializable()) {
			addMagicSerializationMethods(clazz, nonPropertyMethods, allTypesByPrototypicalFullName);
		}

		for (Method method : nonPropertyMethods)
		{
			if (implementedMethodNames.contains(method.getName()))
				method.setImplementationInfo(ImplementationInfo.IMPLEMENTATION_CLAIMED_BY_GENERATED_OBJECT);
		}

		for (Method method : propertyMethods)
		{
			method.setImplementationInfo(ImplementationInfo.IMPLEMENTATION_CLAIMED_BY_GENERATED_OBJECT);
		}

		clazz.initContent(members, propertyMethods, nonPropertyMethods, filterImportTypes(clazz, importTypes), selectedComparableMembers);

		return clazz;
	}

	private void addMagicSerializationMethods(Clazz clazz, List<Method> nonPropertyMethods, Map<String, Type> allTypesByPrototypicalFullName)
	{
		Type noType = new NoType(clazz);

		// Add : private Object readResolve() throws ObjectStreamException :
		Method readResolve = new Method(clazz, AccessLevel.PRIVATE, noType, "readResolve", clazz.getHelperTypes().getJavaLangObjectType(), Collections.emptyList(), Collections.singletonList(new ObjectType(clazz, "java.io.ObjectStreamException")), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		nonPropertyMethods.add(readResolve);

		// Add private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException :
		List<Parameter> readObjectParameters = Collections.singletonList(new Parameter(clazz, new ObjectType(clazz, "java.io.ObjectInputStream"), "in"));
		Method readObject = new Method(clazz, AccessLevel.PRIVATE, noType, "readObject", clazz.getHelperTypes().getVoidType(), readObjectParameters, Arrays.asList(new ObjectType[] { new ObjectType(clazz, "java.io.IOException"), new ObjectType(clazz, "java.lang.ClassNotFoundException") }), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		nonPropertyMethods.add(readObject);

		// Add private void readObjectNoData() throws InvalidObjectException
		Method readObjectNoData = new Method(clazz, AccessLevel.PRIVATE, noType, "readObjectNoData", clazz.getHelperTypes().getVoidType(), Collections.emptyList(), Collections.singletonList(new ObjectType(clazz, "java.io.ObjectStreamException")), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		nonPropertyMethods.add(readObjectNoData);

		// Add : private void writeObject (ObjectOutputStream out) throws IOException :
		List<Parameter> writeObjectParameters = Collections.singletonList(new Parameter(clazz, new ObjectType(clazz, "java.io.ObjectOutputStream"), "out"));
		Method writeObject = new Method(clazz, AccessLevel.PRIVATE, noType, "writeObject", clazz.getHelperTypes().getVoidType(), writeObjectParameters, Collections.singletonList(new ObjectType(clazz, "java.io.IOException")), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		nonPropertyMethods.add(writeObject);

		// Add : private Object writeReplace() throws ObjectStreamException :
		Method writeReplace = new Method(clazz, AccessLevel.PRIVATE, noType, "writeReplace", clazz.getHelperTypes().getJavaLangObjectType(), Collections.emptyList(), Collections.singletonList(new ObjectType(clazz, "java.io.ObjectStreamException")), "", ImplementationInfo.IMPLEMENTATION_MAGIC);
		nonPropertyMethods.add(writeReplace);
	}

	private List<Member> getSelectedComparableMembers(TypeElement masterInterfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer, Map<String, Member> membersByName, List<Member> members) throws Exception
	{
		List<Member> comparableMembers;

		String[] comparableMemberNames = configuration.getComparableMembers();
		if (comparableMemberNames.length==0)
		{
			comparableMembers=members.stream().filter(m -> m.getType().isComparable()).collect(Collectors.toList());
			if (configuration.isComparableEnabled() && comparableMembers.size()!=members.size())
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

	private void processMethod(Types types, Elements elements, TypeElement masterInterfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer, Map<String, Type> allTypesByPrototypicalFullName,
			                   Clazz clazz, Map<String, Member> membersByName, List<Method> nonPropertyMethods,	List<Property> propertyMethods, final StatusHolder statusHolder, ExecutableElement m, DeclaredType interfaceOrClassMirrorType,
			                   Set<String> implementedMethodNames, boolean implementedAlready)
	{
		try {
			String javaDoc = elements.getDocComment(m);
			if (javaDoc==null) // hmmm - seems to be null always (api not working?)
				javaDoc="";

			ExecutableType executableMethodMirrorType = (ExecutableType)types.asMemberOf(interfaceOrClassMirrorType, m);

			Type declaringType = createType(clazz, allTypesByPrototypicalFullName, types, interfaceOrClassMirrorType);

			String methodName = m.getSimpleName().toString();

			TypeMirror returnTypeMirror = executableMethodMirrorType.getReturnType();
			Type returnType = createType(clazz, allTypesByPrototypicalFullName, types, returnTypeMirror);

			List<? extends VariableElement> params =  m.getParameters();
			List<? extends TypeMirror> paramTypes = executableMethodMirrorType.getParameterTypes();

			if (params.size()!=paramTypes.size())
				throw new Exception("Internal error - Numbers of method parameters "+params.size()+" and method parameter types "+paramTypes.size()+" does not match");

			List<? extends TypeMirror> thrownTypeMirrors = executableMethodMirrorType.getThrownTypes();
			List<Type> thrownTypes = thrownTypeMirrors.stream().map(ie -> createType(clazz, allTypesByPrototypicalFullName, types, ie)).collect(Collectors.toList());

			List<Parameter> parameters = new ArrayList<Parameter>();
			for (int i=0; i<params.size(); ++i)
			{
				Parameter param = createParameter(types, elements, allTypesByPrototypicalFullName, clazz, params.get(i), paramTypes.get(i));
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
					Member propertyMember = createPropertyMemberIfValidProperty(clazz, allTypesByPrototypicalFullName, types, interfaceOrClassMirrorType, masterInterfaceElement, returnTypeMirror, params, paramTypes, configuration, m, propertyKind, errorConsumer);

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

			          	Property property = createValidatedProperty(clazz, allTypesByPrototypicalFullName, statusHolder, types, declaringType, m, returnType, parameters, thrownTypes, javaDoc, propertyKind, propertyMember, ImplementationInfo.IMPLEMENTATION_MISSING);

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

	private Parameter createParameter(Types types, Elements elements, Map<String, Type> allTypesByPrototypicalFullName, Clazz clazz, VariableElement param, TypeMirror paramType)
	{
		String name = param.getSimpleName().toString();
		return new Parameter(clazz, createType(clazz, allTypesByPrototypicalFullName, types, paramType), name);
	}

	/**
	 * Create a new type or reuse existing if already created in order to save memoery and processing time.
	 *
	 * @param clazz The class that directly or indirectly references the type
	 * @param allTypesByPrototypicalFullName Pool of previously created types used to avoid duplicates.
	 * @param typeMirrorTypes javax.model helper class
	 * @param mirrorType corresponding javax.model type.
	 *
	 * @return A new or resued Type instance.
	 */
	private Type createType(Clazz clazz, Map<String,Type> allTypesByPrototypicalFullName, Types typeMirrorTypes, TypeMirror mirrorType)
	{
		String typeName = mirrorType.toString();

		// If using self-stand-in, replace with name of generated class and if identical with generate class return clazz itself as type
		typeName=typeName.replace(ThisReference.class.getName(), clazz.getPrototypicalQualifiedName());
		if (typeName.equals(clazz.getPrototypicalQualifiedName()))
			return clazz;

		Type existingType = allTypesByPrototypicalFullName.get(typeName);
		if (existingType!=null) {
			assert(existingType.getClazz()==clazz);
			return existingType;
		}

		Type newType=null;
		if (mirrorType instanceof javax.lang.model.type.PrimitiveType) {
			newType=new com.fortyoneconcepts.valjogen.model.PrimitiveType(clazz, typeName);
			existingType=allTypesByPrototypicalFullName.put(typeName, newType);
		} else if (mirrorType.getKind()==TypeKind.ARRAY) {
		   ArrayType arrayType = (ArrayType)mirrorType;
		   TypeMirror componentTypeMirror = arrayType.getComponentType();
	       Type componentType = createType(clazz, allTypesByPrototypicalFullName, typeMirrorTypes, componentTypeMirror);
	       newType=new com.fortyoneconcepts.valjogen.model.ArrayType(clazz, typeName, componentType);
	       existingType=allTypesByPrototypicalFullName.put(typeName, newType);
		} else {
  	       ObjectType newObjectType;
  	       newType=newObjectType=new com.fortyoneconcepts.valjogen.model.ObjectType(clazz, typeName);
		   existingType=allTypesByPrototypicalFullName.put(typeName, newType);

		   List<? extends TypeMirror> directSuperTypeMirrors = typeMirrorTypes.directSupertypes(mirrorType);

		   Type baseClazzType;
		   List<Type> interfaceTypes;
		   Set<Type> interfaceTypesWithAscendants;
		   if (directSuperTypeMirrors.size()>0) {
			   TypeMirror baseClazzTypeMirror = directSuperTypeMirrors.get(0);
			   baseClazzType = createType(clazz, allTypesByPrototypicalFullName, typeMirrorTypes, baseClazzTypeMirror);

			   List<? extends TypeMirror> interfaceSuperTypeMirrors = directSuperTypeMirrors.size()>1 ? directSuperTypeMirrors.subList(1, directSuperTypeMirrors.size()-1) : Collections.emptyList();
			   interfaceTypes = interfaceSuperTypeMirrors.stream().map(t -> createType(clazz, allTypesByPrototypicalFullName, typeMirrorTypes, t)).collect(Collectors.toList());
			   Stream<? extends TypeMirror> interfaceTypesWithAscendantsTypeMirrors = getSuperTypesWithAncestors(typeMirrorTypes, interfaceSuperTypeMirrors);
			   interfaceTypesWithAscendants = interfaceTypesWithAscendantsTypeMirrors.map(t -> createType(clazz, allTypesByPrototypicalFullName, typeMirrorTypes, t)).collect(Collectors.toSet());
		   } else {
			   baseClazzType=new NoType(clazz);
			   interfaceTypes=Collections.emptyList();
			   interfaceTypesWithAscendants=Collections.emptySet();
		   }

		   List<Type> genericTypeArguments = Collections.emptyList();
		   if (mirrorType instanceof DeclaredType) {
			   DeclaredType declaredType = (DeclaredType)mirrorType;

			   List<? extends TypeMirror> genericTypeMirrorArguments = declaredType.getTypeArguments();

			   genericTypeArguments = genericTypeMirrorArguments.stream().map(t -> createType(clazz, allTypesByPrototypicalFullName, typeMirrorTypes, t)).collect(Collectors.toList());
		   }

		   newObjectType.initType(baseClazzType, interfaceTypes, interfaceTypesWithAscendants, genericTypeArguments);
		}

		assert existingType==null : "Should not overwrite existing type in pool";

		return newType;
	}

	private Stream<? extends TypeMirror> getSuperTypesWithAncestors(Types typeMirrorTypes, List<? extends TypeMirror> superTypes)
	{
		return Stream.concat(superTypes.stream(), superTypes.stream().flatMap(type -> getSuperTypesWithAncestors(typeMirrorTypes, typeMirrorTypes.directSupertypes(type))));
	}

	private DeclaredType createBaseClazzDeclaredType(Elements elements, Types types, TypeElement masterInterfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer, String clazzPackage) throws Exception
	{
		String baseClazzName = configuration.getBaseClazzName();
		if (baseClazzName==null || baseClazzName.isEmpty())
			baseClazzName=ConfigurationDefaults.RootObject;

		return createDeclaredTypeFromString(elements, types, masterInterfaceElement, errorConsumer, baseClazzName, clazzPackage);
	}

	private DeclaredType createDeclaredTypeFromString(Elements elements, Types types, TypeElement masterInterfaceElement, DiagnosticMessageConsumer errorConsumer, String name, String clazzPackage) throws Exception
	{
		String nameWithoutGenerics = NamesUtil.stripGenericQualifier(name);
		nameWithoutGenerics = NamesUtil.ensureQualifedName(nameWithoutGenerics, clazzPackage);

		TypeElement element = elements.getTypeElement(nameWithoutGenerics);
		if (element==null) {
			errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ClassNotFound, name));
			return null; // Abort.
		}

		String[] nameGenericParts = NamesUtil.getGenericQualifierNames(name);
		if (nameGenericParts.length==0)
		{
			TypeMirror elementType = element.asType();
			return (DeclaredType)elementType;
		} else {
			TypeMirror[] genericTypeParts = new TypeMirror[nameGenericParts.length];
			for (int i=0; i<nameGenericParts.length; ++i)
			{
				String nameGenericPart = nameGenericParts[i];
				nameGenericPart = NamesUtil.ensureQualifedName(nameGenericPart, clazzPackage);

				TypeElement genericElement = elements.getTypeElement(nameGenericPart);
				if (genericElement==null) {
					errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ClassNotFound, nameGenericPart));
					return (DeclaredType)element.asType();
				}

				TypeMirror genericElementType = genericElement.asType();
				genericTypeParts[i]=(DeclaredType)genericElementType;
			}

			DeclaredType result = types.getDeclaredType(element, genericTypeParts);
		    return result;
		}
	}

	private List<Type> createImportTypes(Clazz clazz, Map<String,Type> allTypesByPrototypicalFullName, Types types, Elements elements, TypeElement masterInterfaceElement, Configuration configuration, DeclaredType baseClazzDeclaredType, List<DeclaredType> implementedDecalredInterfaceTypes, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		List<Type> importTypes = new ArrayList<Type>();
		for (DeclaredType implementedInterfaceDeclaredType : implementedDecalredInterfaceTypes)
		  importTypes.add(createType(clazz, allTypesByPrototypicalFullName, types, implementedInterfaceDeclaredType));

		importTypes.add(createType(clazz, allTypesByPrototypicalFullName, types, baseClazzDeclaredType));

		for (String importName : configuration.getImportClasses())
		{
			TypeElement importElement = elements.getTypeElement(importName);
			if (importElement==null) {
				errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ImportTypeNotFound, importName));
			} else {
			   Type importElementType = createType(clazz, allTypesByPrototypicalFullName, types, importElement.asType());
			   importTypes.add(importElementType);
			}
		}
		return importTypes;
	}

	private List<DeclaredType> createInterfaceDeclaredTypes(Elements elements, Types types, TypeElement masterInterfaceElement, DeclaredType masterInterfaceType, String[] ekstraInterfaceNames, DiagnosticMessageConsumer errorConsumer, String clazzPackage) throws Exception
	{
		List<DeclaredType> interfaceElements = new ArrayList<DeclaredType>();
		interfaceElements.add(masterInterfaceType);
		for (int i=0; i<ekstraInterfaceNames.length; ++i)
		{
			String ekstraInterfaceName = ekstraInterfaceNames[i];
			if (!ekstraInterfaceName.isEmpty())
			{
				DeclaredType extraDeclaredType = createDeclaredTypeFromString(elements, types, masterInterfaceElement, errorConsumer, ekstraInterfaceName, clazzPackage);
				interfaceElements.add(extraDeclaredType);
			}
		}
		return interfaceElements;
	}


	private Property createValidatedProperty(Clazz clazz, Map<String,Type> allTypesByPrototypicalFullName, StatusHolder statusHolder, Types types, Type declaringType, ExecutableElement m, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, PropertyKind propertyKind, Member propertyMember, ImplementationInfo implementationInfo)
	{
		Property property;

		String propertyName = m.getSimpleName().toString();

		if (parameters.size()==0) {
			property=new Property(clazz, AccessLevel.PUBLIC, declaringType, propertyName, returnType, thrownTypes, propertyMember, propertyKind, javaDoc, implementationInfo);
		} else if (parameters.size()==1) {
			Parameter parameter = parameters.get(0);

			// Parameter names may be syntesised so we can fall back on using member
			// name as parameter name. Note if this happen so we can issue a warning later.
			if (parameter.getName().matches("arg\\d+")) { // Synthesised check (not bullet-proof):
				statusHolder.encountedSynthesisedMembers=true;
				parameter=parameter.setName(propertyMember.getName());
			}

			property = new Property(clazz, AccessLevel.PUBLIC, declaringType, propertyName, returnType, thrownTypes, propertyMember, propertyKind, javaDoc, implementationInfo, parameter);
		} else throw new RuntimeException("Unexpected number of formal parameters for property "+m.toString()); // Should not happen for a valid propety unless validation above has a programming error.

		return property;
	}

	private List<Type> filterImportTypes(Clazz clazz, List<Type> importTypes)
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

	private String createQualifiedClassName(Configuration configuration, String qualifedInterfaceName, String sourcePackageName)
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

	private Stream<DeclaredType> getDeclaredInterfacesWithAscendents(Types types, DeclaredType classOrInterfaceType)
	{
		TypeElement classOrInterfaceElement = (TypeElement)classOrInterfaceType.asElement();
		return Stream.concat(classOrInterfaceElement.getInterfaces().stream()
				             .map(t -> (DeclaredType)t)
				             .flatMap(z -> getDeclaredInterfacesWithAscendents(types, z)), Stream.of(classOrInterfaceType));
	}

	private Stream<DeclaredType> getSuperTypesWithAscendents(Types types, DeclaredType classOrInterfaceType)
	{
		List<? extends TypeMirror> superTypes = types.directSupertypes(classOrInterfaceType);
		Stream<DeclaredType> superTypesAsDeclaredTypes = superTypes.stream().map(t -> (DeclaredType)t);
		return Stream.concat(superTypesAsDeclaredTypes, Stream.of(classOrInterfaceType));

	}

	private Member createPropertyMemberIfValidProperty(Clazz clazz, Map<String,Type> allTypesByPrototypicalFullName, Types types, DeclaredType interfaceOrClassMirrorType, TypeElement interfaceElement,
			                                           TypeMirror returnTypeMirror, List<? extends VariableElement> setterParams, List<? extends TypeMirror> setterParamTypes,
			                                           Configuration configuration, ExecutableElement methodElement, PropertyKind kind, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		TypeMirror propertyTypeMirror;

		if (kind==PropertyKind.GETTER) {
			if (setterParams.size()!=0) {
				if (!configuration.isMalformedPropertiesIgnored())
				  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedGetter, methodElement.toString()));
				return null;
			}

			propertyTypeMirror = returnTypeMirror;
			return new Member(clazz, createType(clazz, allTypesByPrototypicalFullName, types, propertyTypeMirror), syntesisePropertyMemberName(configuration.getGetterPrefixes(), methodElement));
		} else if (kind==PropertyKind.SETTER) {
			if (setterParams.size()!=1) {
				if (!configuration.isMalformedPropertiesIgnored())
  				  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedSetter, methodElement.toString()));
				return null;
			}

			String returnTypeName = returnTypeMirror.toString();

			String declaredInterfaceTypeName = interfaceOrClassMirrorType.toString();
			if (!returnTypeName.equals("void") && !returnTypeName.equals(declaredInterfaceTypeName) && !returnTypeName.equals(clazz.getQualifiedName())) {
				if (!configuration.isMalformedPropertiesIgnored())
					  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedSetter, methodElement.toString()));
				return null;
			}

			propertyTypeMirror=setterParamTypes.get(0); // setterParams.get(0).asType();
			return new Member(clazz, createType(clazz, allTypesByPrototypicalFullName, types, propertyTypeMirror), syntesisePropertyMemberName(configuration.getSetterPrefixes(), methodElement));
		} else {
			return null; // Not a proeprty.
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

	public static boolean isClass(TypeMirror typeMirror)
	{
		ElementKind kind = (typeMirror instanceof DeclaredType) ? ((DeclaredType) typeMirror).asElement().getKind() : ElementKind.OTHER;
		return kind==ElementKind.CLASS || kind==ElementKind.ENUM;
    }

	public static boolean isInterface(TypeMirror typeMirror)
	{
		ElementKind kind = (typeMirror instanceof DeclaredType) ? ((DeclaredType) typeMirror).asElement().getKind() : ElementKind.OTHER;
        return kind == ElementKind.INTERFACE;
    }
}
