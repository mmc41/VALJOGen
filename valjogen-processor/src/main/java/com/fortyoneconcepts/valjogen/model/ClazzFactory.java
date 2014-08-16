/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/***
 * Create a Clazz instance along with all its dependencies by inspecting
 * javax.model metadata and annotation provided annotation processor.
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

	private ClazzFactory() {}

	public Clazz createClazz(Types types, Elements elements,TypeElement element, Configuration configuration, Consumer<String> errorConsumer)
	{
		PackageElement sourcePackageElement = elements.getPackageOf(element);

		String sourceInterfacePackageName = sourcePackageElement.isUnnamed() ? "" : sourcePackageElement.toString();

		String baseClazzName = configuration.getBaseClazzName();
		if (baseClazzName.isEmpty() || baseClazzName.equals(ConfigurationDefaults.NotApplicable))
			baseClazzName=ConfigurationDefaults.RootObject;

		TypeElement baseClazzElement = elements.getTypeElement(baseClazzName);
		if (baseClazzElement==null) {
			errorConsumer.accept("Could not find base class "+baseClazzName);
			throw new RuntimeException("Unknown base class "+baseClazzName);
		}

		TypeMirror baseClazzType = baseClazzElement.asType();
		TypeMirror interfaceType = element.asType();

		String className = createQualifiedClassName(configuration, interfaceType.toString(), sourceInterfacePackageName);

		String classJavaDoc = elements.getDocComment(element);
		if (classJavaDoc==null)
			classJavaDoc="";

		Clazz clazz = new Clazz(configuration, types, elements, className, element.asType(), baseClazzType, classJavaDoc);

		Map<String, Member> membersByName = new LinkedHashMap<String, Member>();
		List<Method> nonPropertyMethods = new ArrayList<Method>();
		List<Property> propertyMethods= new ArrayList<Property>();
		List<Type> importTypes = new ArrayList<Type>();

		// Collect all members, property methods and non-property methods from interfaces:
		// Nb. Stream.forEach has side-effects so is not thread-safe and will not work with parallel streams - but do not need to anyway.
		final Stream<TypeElement> allInterfaces = getInterfacesWithDecendents(types, element);

		// Define helper types

		TypeElement arraysElement = elements.getTypeElement("java.util.Arrays");
		TypeElement objectsElement = elements.getTypeElement("java.util.Objects");

		Type arraysType = new Type(clazz, arraysElement.asType());
		Type objectsType = new Type(clazz, objectsElement.asType());

		HelperTypes helperTypes = new HelperTypes(arraysType, objectsType);

		// Import interface, base class and specified extras:
		importTypes.add(new Type(clazz, interfaceType));
		importTypes.add(new Type(clazz, baseClazzType));
		for (String importName : configuration.getImportClasses())
		{
			TypeElement importElement = elements.getTypeElement(importName);
			if (importElement==null) {
				errorConsumer.accept("Unknown type "+importName);
			} else {
			   Type importElementType = new Type(clazz, importElement.asType());
			   importTypes.add(importElementType);
			}
		}

		// Look at all methods in all reachable interfaces:
		allInterfaces.flatMap(i -> i.getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD).map(m -> (ExecutableElement)m).filter(em -> !em.isDefault()))
		             .forEach(m -> {
						Member propertyMember = createPropertyMemberIfValidProperty(clazz, m, errorConsumer);

						String javaDoc = elements.getDocComment(m);
						if (javaDoc==null)
							javaDoc="";

						if (propertyMember!=null) {
			                final Member existingMember = membersByName.putIfAbsent(propertyMember.getName(), propertyMember);
			              	if (existingMember!=null)
			              	   propertyMember = existingMember;

			              	Property property;
			              	List<? extends VariableElement> parameterElements = m.getParameters();
			              	if (parameterElements.size()==0)
			              		property=new Property(clazz, m, propertyMember, javaDoc);
			              	else property = new Property(clazz, m, propertyMember, javaDoc, new Parameter(clazz, m.getParameters().get(0), propertyMember.getName()));

			              	propertyMember.addPropertyMethod(property);
			              	propertyMethods.add(property);
						} else {
							List<Parameter> parameters = m.getParameters().stream().map(p -> new Parameter(clazz, p)).collect(Collectors.toList());
							nonPropertyMethods.add(new Method(clazz, m, parameters, javaDoc));
						}
					 });

		clazz.setHelperTypes(helperTypes);
        clazz.setPropertyMethods(propertyMethods);
        clazz.setNonPropertyMethods(nonPropertyMethods);
        clazz.setMembers(new ArrayList<Member>(membersByName.values()));
        clazz.setImportTypes(filterImportTypes(clazz, importTypes));

		return clazz;
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

	private static Member createPropertyMemberIfValidProperty(Clazz clazz, ExecutableElement methodElement, Consumer<String> errorConsumer)
	{
		TypeMirror propertyType;

		String methodName = methodElement.getSimpleName().toString();

		if (NamesUtil.isGetterMethod(methodName)) {
			// TODO: Check if correct non-void type.
			propertyType = methodElement.getReturnType();
		} else if (NamesUtil.isSetterMethod(methodName)) {
			List<? extends VariableElement> setterParams = methodElement.getParameters();

			if (setterParams.size()!=1) {
				errorConsumer.accept("Malformed setter "+methodElement.toString());
				return null;
			} else {
				propertyType=setterParams.get(0).asType();
			}

		} else {
			return null; // Not a proeprty.
		}

		return new Member(clazz, new Type(clazz, propertyType), syntesisePropertyMemberName(methodElement));
	}

	private static String syntesisePropertyMemberName(ExecutableElement method)
	{
		String name = method.getSimpleName().toString();

		int skip;
		if (name.startsWith("is"))
			skip=2;
		else if (name.startsWith("get") || name.startsWith("set"))
			skip=3;
		else skip=0;

		if (name.length()>skip)
			name=name.substring(skip);

		name=Introspector.decapitalize(name);
		name=NamesUtil.makeSafeJavaIdentifier(name);

		return name;
	}
}
