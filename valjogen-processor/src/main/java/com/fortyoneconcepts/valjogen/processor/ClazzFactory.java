/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.beans.Introspector;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	/**
    * Create a Clazz model instance along with all its dependen model instancess by inspecting
    * javax.model metadata and the configuration provided by annotation(s) read by annotation processor.
    *
	* @param types Utility instance provided by javax.lang.model framework.
	* @param elements Utility instance provided by javax.lang.model framework.
	* @param masterInterfaceElement The interface that has been selected for code generation (by an annotation).
	* @param configuration Descripes the user-selected details about what should be generated (combination of annotation(s) and annotation processor setup).
	* @param errorConsumer Where to report errors and warning
	*
	* @return A initialized Clazz which is a model for what our generated code should look like.
	*
	* @throws Exception if a fatal error has occured.
	*/
	public Clazz createClazz(Types types, Elements elements, TypeElement masterInterfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		// Get the interface's .class abstraction:
		DeclaredType masterInterfaceDecl = (DeclaredType)masterInterfaceElement.asType();

		Map<String,Type> allTypesByPrototypicalFullName = new HashMap<String, Type>();

		PackageElement sourcePackageElement = elements.getPackageOf(masterInterfaceElement);

		String sourceInterfacePackageName = sourcePackageElement.isUnnamed() ? "" : sourcePackageElement.toString();
		String className = createQualifiedClassName(configuration, masterInterfaceElement.asType().toString(), sourceInterfacePackageName);
		String classPackage = NamesUtil.getPackageFromQualifiedName(className);

		DeclaredType baseClazzDeclaredMirrorType = createBaseClazzDeclaredType(elements, types, masterInterfaceElement, configuration, errorConsumer, classPackage);
		if (baseClazzDeclaredMirrorType==null)
			return null;

		String[] ekstraInterfaceNames = configuration.getExtraInterfaces();

		List<DeclaredType> interfaceDeclaredMirrorTypes = createInterfaceDeclaredTypes(elements, types, masterInterfaceElement, masterInterfaceDecl, ekstraInterfaceNames, errorConsumer, classPackage);
		List<DeclaredType> allInterfaceDeclaredMirrorTypes = interfaceDeclaredMirrorTypes.stream().flatMap(ie -> getDeclaredInterfacesWithDecendents(types, ie)).collect(Collectors.toList());

		String classJavaDoc = elements.getDocComment(masterInterfaceElement);
		if (classJavaDoc==null)
			classJavaDoc="";

		Clazz clazz = new Clazz(configuration, className, classJavaDoc);


	    List<? extends TypeMirror> typeArgs = masterInterfaceDecl.getTypeArguments();

	    List<Type> typeArgTypes = typeArgs.stream().map(t -> createType(clazz, allTypesByPrototypicalFullName, types, t)).collect(Collectors.toList());

		// List<GenericParameter> genericParameters = masterInterfaceElement.getTypeParameters().stream().map(p -> createGenericParameter(clazz, allTypesByPrototypicalFullName, types, p)).collect(Collectors.toList());

		Type baseClazzType = createType(clazz, allTypesByPrototypicalFullName, types, baseClazzDeclaredMirrorType);
		List<Type> interfaceTypes = interfaceDeclaredMirrorTypes.stream().map(ie -> createType(clazz, allTypesByPrototypicalFullName, types, ie)).collect(Collectors.toList());
		Set<Type> interfaceTypesWithAscendants = allInterfaceDeclaredMirrorTypes.stream().map(ie -> createType(clazz, allTypesByPrototypicalFullName, types, ie)).collect(Collectors.toSet());

		clazz.initType(baseClazzType, interfaceTypes, interfaceTypesWithAscendants, typeArgTypes);

		Map<String, Member> membersByName = new LinkedHashMap<String, Member>();
		List<Method> nonPropertyMethods = new ArrayList<Method>();
		List<Property> propertyMethods= new ArrayList<Property>();

		// Import interface(s), base class and specified extras:
		List<Type> importTypes = createImportTypes(clazz, allTypesByPrototypicalFullName, types, elements, masterInterfaceElement, configuration, baseClazzDeclaredMirrorType, interfaceDeclaredMirrorTypes, errorConsumer);

		final StatusHolder statusHolder = new StatusHolder();

		// Collect all members, property methods and non-property methods from interfaces:
		// Nb. Stream.forEach has side-effects so is not thread-safe and will not work with parallel streams - but do not need to anyway.
		allInterfaceDeclaredMirrorTypes.stream().flatMap(i -> i.asElement().getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD).map(m -> (ExecutableElement)m).filter(em -> !em.isDefault()))
		             .forEach(m -> {
		            	try {
							String javaDoc = elements.getDocComment(m);
							if (javaDoc==null)
								javaDoc="";

							boolean captured = false;

							Type declaringType = createType(clazz, allTypesByPrototypicalFullName, types, m.getEnclosingElement().asType());

		            		String methodName = m.getSimpleName().toString();
		            		Type returnType = createType(clazz, allTypesByPrototypicalFullName, types, m.getReturnType());

		            		PropertyKind propertyKind = null;
		                    if (NamesUtil.isGetterMethod(methodName, configuration.getGetterPrefixes()))
		                    	propertyKind=PropertyKind.GETTER;
		                    else if (NamesUtil.isSetterMethod(methodName, configuration.getSetterPrefixes()))
		                    	propertyKind=PropertyKind.SETTER;

							if (propertyKind!=null) {
								Member propertyMember = createPropertyMemberIfValidProperty(clazz, allTypesByPrototypicalFullName, types, masterInterfaceElement, configuration, m, propertyKind, errorConsumer);

								if (propertyMember!=null) {
					                final Member existingMember = membersByName.putIfAbsent(propertyMember.getName(), propertyMember);
					              	if (existingMember!=null)
					              	   propertyMember = existingMember;

					              	Property property = createValidatedProperty(clazz, allTypesByPrototypicalFullName, statusHolder, types, declaringType, m, returnType, javaDoc,	propertyKind, propertyMember);

					              	propertyMember.addPropertyMethod(property);
					              	propertyMethods.add(property);
					              	captured=true;
								}
							}

							if (!captured)
							{


								List<Parameter> parameters = m.getParameters().stream().map(p -> createParameter(types,	elements, allTypesByPrototypicalFullName, clazz, p)).collect(Collectors.toList());
								List<Type> typeParameters = m.getTypeParameters().stream().map(p -> createType(clazz, allTypesByPrototypicalFullName, types, p.asType())).collect(Collectors.toList());

								nonPropertyMethods.add(new Method(clazz, declaringType, methodName, returnType, parameters, typeParameters, javaDoc));
							}
		            	} catch (Exception e)
		            	{
		            		String location = m.getEnclosingElement().toString()+"."+m.getSimpleName().toString();
		            		throw new RuntimeException("Failure during processing of "+location+" due to "+e.getMessage(), e);
		            	}
					 });

		if (statusHolder.encountedSynthesisedMembers)
			errorConsumer.message(masterInterfaceElement, Kind.WARNING, String.format(ProcessorMessages.ParameterNamesUnavailable, masterInterfaceElement.toString()));

		clazz.initContent(new ArrayList<Member>(membersByName.values()), propertyMethods, nonPropertyMethods, filterImportTypes(clazz, importTypes));

		return clazz;
	}

	private Parameter createParameter(Types types, Elements elements, Map<String, Type> allTypesByPrototypicalFullName, Clazz clazz, VariableElement p)
	{
		String name = p.getSimpleName().toString();
		TypeMirror pTypeMirror = p.asType();

		/*
		if (p instanceof TypeParameterElement)
		{
			TypeParameterElement te = (TypeParameterElement)p;
			String s = te.getSimpleName().toString();
		}


		if (pTypeMirror.toString().equals("T")){
			TypeVariable variableTypeMirror = (TypeVariable)pTypeMirror;

			Element e = variableTypeMirror.asElement();
			String n = e.getSimpleName().toString();

			TypeMirror lower =variableTypeMirror.getLowerBound();
			TypeMirror higher =variableTypeMirror.getUpperBound();

			String l = lower.toString();
			String h = higher.toString();

			System.out.println("bla: "+e);
		}*/

		return new Parameter(clazz, createType(clazz, allTypesByPrototypicalFullName, types, pTypeMirror), name);
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
		typeName=typeName.replace(SelfReference.class.getName(), clazz.getPrototypicalQualifiedName());
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

		   /*
		   if (mirrorType instanceof TypeVariable) {
				TypeVariable genericMirrorType = (TypeVariable)mirrorType;
				// System.out.println(typeName);
		   }*/
		}

		assert existingType==null : "Should not overwrite existing type in pool";

		return newType;
	}

	private GenericParameter createGenericParameter(Clazz clazz, Map<String, Type> allTypesByPrototypicalFullName, Types types, TypeParameterElement typeELement)
	{
		TypeMirror parameterMirrorType = typeELement.asType();
		String genericParameterName = parameterMirrorType.toString();
		return new GenericParameter(clazz, genericParameterName);
		/*

		Element genericElement = typeELement.getGenericElement();
	//	List<? extends TypeMirror> bounds = typeELement.getBounds();
	//	List<String> bs = bounds.stream().map(b -> b.toString()).collect(Collectors.toList());

		TypeMirror genericMirrorType = genericElement.asType();

		final String typeName = genericMirrorType.toString();

		Type genericType = new com.fortyoneconcepts.valjogen.model.ObjectType(clazz, typeName);

		return genericType;
		*/
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


	private Property createValidatedProperty(Clazz clazz, Map<String,Type> allTypesByPrototypicalFullName, StatusHolder statusHolder, Types types, Type declaringType, ExecutableElement m, Type returnType, String javaDoc, PropertyKind propertyKind, Member propertyMember)
	{
		List<Type> typeParameters = m.getTypeParameters().stream().map(p -> createType(clazz, allTypesByPrototypicalFullName, types, p.asType())).collect(Collectors.toList());

		Property property;
		List<? extends VariableElement> parameterElements = m.getParameters();

		String propertyName = m.getSimpleName().toString();

		if (parameterElements.size()==0) {
			property=new Property(clazz, declaringType, propertyName, returnType, propertyMember, propertyKind, javaDoc, typeParameters);
		} else if (parameterElements.size()==1) {
			VariableElement varElement = m.getParameters().get(0);

			String varElementName = varElement.getSimpleName().toString();

			// Parameter names may be syntesised so we may need to fall back on using member
			// name as parameter name. Note if this happen so we can issue a warning later.
			String usedVarElementName;
			if (varElementName.matches("arg\\d+")) { // Synthesised check (not bullet-proof):
				statusHolder.encountedSynthesisedMembers=true;
				usedVarElementName=propertyMember.getName();
			} else usedVarElementName=varElementName;

			property = new Property(clazz, declaringType, propertyName, returnType, propertyMember, propertyKind, javaDoc, new Parameter(clazz, createType(clazz, allTypesByPrototypicalFullName, types, varElement.asType()), usedVarElementName), typeParameters);
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

	private Stream<DeclaredType> getDeclaredInterfacesWithDecendents(Types types, DeclaredType interfaceType)
	{
		TypeElement interfaceElement = (TypeElement)interfaceType.asElement();
		return Stream.concat(interfaceElement.getInterfaces().stream()
				             .map(t -> (DeclaredType)t)
				             .flatMap(z -> getDeclaredInterfacesWithDecendents(types, z)), Stream.of(interfaceType));
	}

	private Member createPropertyMemberIfValidProperty(Clazz clazz, Map<String,Type> allTypesByPrototypicalFullName, Types types, TypeElement interfaceElement, Configuration configuration, ExecutableElement methodElement, PropertyKind kind, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		TypeMirror propertyType;

		List<? extends VariableElement> setterParams = methodElement.getParameters();

		if (kind==PropertyKind.GETTER) {
			if (setterParams.size()!=0) {
				if (!configuration.isMalformedPropertiesIgnored())
				  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedGetter, methodElement.toString()));
				return null;
			}

			TypeMirror returnType = methodElement.getReturnType();

			propertyType = returnType;
			return new Member(clazz, createType(clazz, allTypesByPrototypicalFullName, types, propertyType), syntesisePropertyMemberName(configuration.getGetterPrefixes(), methodElement));
		} else if (kind==PropertyKind.SETTER) {
			if (setterParams.size()!=1) {
				if (!configuration.isMalformedPropertiesIgnored())
  				  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedSetter, methodElement.toString()));
				return null;
			}

			TypeMirror returnType = methodElement.getReturnType();
			String returnTypeName = returnType.toString();

			if (!returnTypeName.equals("void") && !returnTypeName.equals(interfaceElement.toString()) && !returnTypeName.equals(clazz.getQualifiedName())) {
				if (!configuration.isMalformedPropertiesIgnored())
					  errorConsumer.message(methodElement, Kind.ERROR, String.format(ProcessorMessages.MalFormedSetter, methodElement.toString()));
				return null;
			}

			propertyType=setterParams.get(0).asType();
			return new Member(clazz, createType(clazz, allTypesByPrototypicalFullName, types, propertyType), syntesisePropertyMemberName(configuration.getSetterPrefixes(), methodElement));
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
