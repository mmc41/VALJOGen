/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.util.*;
import javax.tools.Diagnostic.Kind;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.util.*;

/***
 * This class is responsible for transforming data in the javax.model.* format to our own valjogen models.
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
	* @param interfaceElement The interface that has been selected for code generation (by an annotation).
	* @param configuration Descripes the user-selected details about what should be generated (combination of annotation(s) and annotation processor setup).
	* @param errorConsumer Where to report errors and warning
	*
	* @return A initialized Clazz which is a model for what our generated code should look like.
	*
	* @throws Exception if a fatal error has occured.
	*/
	public Clazz createClazz(Types types, Elements elements, TypeElement interfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		PackageElement sourcePackageElement = elements.getPackageOf(interfaceElement);

		String sourceInterfacePackageName = sourcePackageElement.isUnnamed() ? "" : sourcePackageElement.toString();

		TypeMirror baseClazzTypeMirror = createBaseClazzType(elements,interfaceElement, configuration, errorConsumer);
		if (baseClazzTypeMirror==null)
			return null;

		String[] ekstraInterfaceNames = configuration.getExtraInterfaces();

		List<TypeElement> interfaceElements = createInterfaceElements(elements,	interfaceElement, ekstraInterfaceNames, errorConsumer);

		List<TypeMirror> interfaceTypeMirrors = interfaceElements.stream().map(ie -> ie.asType()).collect(Collectors.toList());

		String className = createQualifiedClassName(configuration, interfaceElement.asType().toString(), sourceInterfacePackageName);

		String classJavaDoc = elements.getDocComment(interfaceElement);
		if (classJavaDoc==null)
			classJavaDoc="";

		Clazz clazz = new Clazz(configuration, className, classJavaDoc);

		Type baseClazzType = createType(clazz, baseClazzTypeMirror);
		List<Type> interfaceTypes = interfaceTypeMirrors.stream().map(it -> createType(clazz, it)).collect(Collectors.toList());

		clazz.setBaseClazzType(baseClazzType);
		clazz.setInterfaceTypes(interfaceTypes);

		Map<String, Member> membersByName = new LinkedHashMap<String, Member>();
		List<Method> nonPropertyMethods = new ArrayList<Method>();
		List<Property> propertyMethods= new ArrayList<Property>();

		// Define helper types
		TypeElement arraysElement = elements.getTypeElement("java.util.Arrays");
		TypeElement objectsElement = elements.getTypeElement("java.util.Objects");

		Type arraysType = createType(clazz, arraysElement.asType());
		Type objectsType = createType(clazz, objectsElement.asType());

		HelperTypes helperTypes = new HelperTypes(arraysType, objectsType);

		// Import interface(s), base class and specified extras:
		List<Type> importTypes = createImportTypes(clazz, elements, interfaceElement, configuration, baseClazzTypeMirror, interfaceTypeMirrors, errorConsumer);

		final StatusHolder statusHolder = new StatusHolder();

		// Get stream of all interfaces that we need to deal with
		final Stream<TypeElement> allInterfaces = interfaceElements.stream().flatMap(ie -> getInterfacesWithDecendents(types, ie));

		// Collect all members, property methods and non-property methods from interfaces:
		// Nb. Stream.forEach has side-effects so is not thread-safe and will not work with parallel streams - but do not need to anyway.
		allInterfaces.flatMap(i -> i.getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD).map(m -> (ExecutableElement)m).filter(em -> !em.isDefault()))
		             .forEach(m -> {
		            	try {
							String javaDoc = elements.getDocComment(m);
							if (javaDoc==null)
								javaDoc="";

							boolean captured = false;

		            		String methodName = m.getSimpleName().toString();
		            		Type returnType = createType(clazz, m.getReturnType());

		            		PropertyKind propertyKind = null;
		                    if (NamesUtil.isGetterMethod(methodName, configuration.getGetterPrefixes()))
		                    	propertyKind=PropertyKind.GETTER;
		                    else if (NamesUtil.isSetterMethod(methodName, configuration.getSetterPrefixes()))
		                    	propertyKind=PropertyKind.SETTER;

							if (propertyKind!=null) {
								Member propertyMember = createPropertyMemberIfValidProperty(clazz, interfaceElement, configuration, m, propertyKind, errorConsumer);

								if (propertyMember!=null) {
					                final Member existingMember = membersByName.putIfAbsent(propertyMember.getName(), propertyMember);
					              	if (existingMember!=null)
					              	   propertyMember = existingMember;

					              	Property property = createValidatedProperty(clazz, statusHolder, m, returnType, javaDoc,	propertyKind, propertyMember);

					              	propertyMember.addPropertyMethod(property);
					              	propertyMethods.add(property);
					              	captured=true;
								}
							}

							if (!captured)
							{
								List<Parameter> parameters = m.getParameters().stream().map(p -> new Parameter(clazz, createType(clazz, p.asType()), p.getSimpleName().toString())).collect(Collectors.toList());
								nonPropertyMethods.add(new Method(clazz, methodName, returnType, parameters, javaDoc));
							}
		            	} catch (Exception e)
		            	{
		            		throw new RuntimeException("Failure during processing", e);
		            	}
					 });

		if (statusHolder.encountedSynthesisedMembers)
			errorConsumer.message(interfaceElement, Kind.WARNING, String.format(ProcessorMessages.ParameterNamesUnavailable, interfaceElement.toString()));

		clazz.setHelperTypes(helperTypes);
        clazz.setPropertyMethods(propertyMethods);
        clazz.setNonPropertyMethods(nonPropertyMethods);
        clazz.setMembers(new ArrayList<Member>(membersByName.values()));
        clazz.setImportTypes(filterImportTypes(clazz, importTypes));

		return clazz;
	}

	private Type createType(Clazz clazz, TypeMirror type)
	{
		if (type instanceof javax.lang.model.type.PrimitiveType) {
			return new com.fortyoneconcepts.valjogen.model.PrimitiveType(clazz, type.toString());
		} else if (type.getKind()==TypeKind.ARRAY) {
			ArrayType arrayType = (ArrayType)type;
			TypeMirror componentTypeMirror = arrayType.getComponentType();
	        Type componentType = createType(clazz, componentTypeMirror);
			return new com.fortyoneconcepts.valjogen.model.ArrayType(clazz, type.toString(), componentType);
		} else {
		    return new com.fortyoneconcepts.valjogen.model.ObjectType(clazz, type.toString());
		}
	}

	private TypeMirror createBaseClazzType(Elements elements, TypeElement interfaceElement, Configuration configuration, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		String baseClazzName = configuration.getBaseClazzName();
		if (baseClazzName.isEmpty() || baseClazzName.equals(ConfigurationDefaults.NotApplicable))
			baseClazzName=ConfigurationDefaults.RootObject;

		TypeElement baseClazzElement = elements.getTypeElement(baseClazzName);
		if (baseClazzElement==null) {
			errorConsumer.message(interfaceElement, Kind.ERROR, String.format(ProcessorMessages.BaseClassNotFound, baseClazzName));
			return null; // Abort.
		}

		TypeMirror baseClazzType = baseClazzElement.asType();

		return baseClazzType;
	}

	private List<Type> createImportTypes(Clazz clazz, Elements elements, TypeElement interfaceElement, Configuration configuration, TypeMirror baseClazzType, List<TypeMirror> interfaceTypes, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		List<Type> importTypes = new ArrayList<Type>();
		for (TypeMirror interfaceType : interfaceTypes)
		  importTypes.add(createType(clazz, interfaceType));

		importTypes.add(createType(clazz, baseClazzType));

		for (String importName : configuration.getImportClasses())
		{
			TypeElement importElement = elements.getTypeElement(importName);
			if (importElement==null) {
				errorConsumer.message(interfaceElement, Kind.ERROR, String.format(ProcessorMessages.ImportTypeNotFound, importName));
			} else {
			   Type importElementType = createType(clazz, importElement.asType());
			   importTypes.add(importElementType);
			}
		}
		return importTypes;
	}

	private List<TypeElement> createInterfaceElements(Elements elements, TypeElement interfaceElement, String[] ekstraInterfaceNames, DiagnosticMessageConsumer errorConsumer) throws Exception
	{
		List<TypeElement> interfaceElements = new ArrayList<TypeElement>();
		interfaceElements.add(interfaceElement);
		for (int i=0; i<ekstraInterfaceNames.length; ++i)
		{
			String ekstraInterfaceName = ekstraInterfaceNames[i];
			if (!ekstraInterfaceName.isEmpty() && !ekstraInterfaceName.equals(ConfigurationDefaults.NotApplicable))
			{
				TypeElement ektra = elements.getTypeElement(ekstraInterfaceName);
				if (ektra==null) {
					errorConsumer.message(interfaceElement, Kind.ERROR, String.format(ProcessorMessages.InterfaceNotFound, ekstraInterfaceName));
				}
				interfaceElements.add(ektra);
			}
		}
		return interfaceElements;
	}


	private Property createValidatedProperty(Clazz clazz, final StatusHolder statusHolder, ExecutableElement m, Type returnType, String javaDoc, PropertyKind propertyKind, Member propertyMember)
	{
		Property property;
		List<? extends VariableElement> parameterElements = m.getParameters();

		String propertyName = m.getSimpleName().toString();

		if (parameterElements.size()==0) {
			property=new Property(clazz, propertyName, returnType, propertyMember, propertyKind, javaDoc);
		} else if (parameterElements.size()==1) {
			VariableElement varElement = m.getParameters().get(0);

			String varElementName = varElement.getSimpleName().toString();

			// Parameter names may be syntesised so we may need to fall back on using member
			// name as parameter name. Note if this happen so we can issue a warning later.
			String usedVarElementName;
			if (varElementName.startsWith("arg")) { // Synthesised check (not bullet-proof).
				statusHolder.encountedSynthesisedMembers=true;
				usedVarElementName=propertyMember.getName();
			} else usedVarElementName=varElementName;

			property = new Property(clazz, propertyName, returnType, propertyMember, propertyKind, javaDoc, new Parameter(clazz, createType(clazz, varElement.asType()), usedVarElementName));
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
		if (className.isEmpty() || className.equals(ConfigurationDefaults.NotApplicable))
			className = NamesUtil.createNewClassNameFromInterfaceName(qualifedInterfaceName);

		if (!NamesUtil.isQualified(className))
		{
			String packageName = configuration.getPackage();
			if (packageName.equals(ConfigurationDefaults.NotApplicable))
				packageName=sourcePackageName;

			if (!packageName.isEmpty())
				className=packageName+"."+className;
		}

		return className;
	}

	private Stream<TypeElement> getInterfacesWithDecendents(Types types, TypeElement element)
	{
		return Stream.concat(element.getInterfaces().stream()
				             .map(t -> (TypeElement)types.asElement(t))
				             .flatMap(z -> getInterfacesWithDecendents(types, z)), Stream.of(element));
	}

	private Member createPropertyMemberIfValidProperty(Clazz clazz, TypeElement interfaceElement, Configuration configuration, ExecutableElement methodElement, PropertyKind kind, DiagnosticMessageConsumer errorConsumer) throws Exception
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
			return new Member(clazz, createType(clazz, propertyType), syntesisePropertyMemberName(configuration.getGetterPrefixes(), methodElement));
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
			return new Member(clazz, createType(clazz, propertyType), syntesisePropertyMemberName(configuration.getSetterPrefixes(), methodElement));
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
}
