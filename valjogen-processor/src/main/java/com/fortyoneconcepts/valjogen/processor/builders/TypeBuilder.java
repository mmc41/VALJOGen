/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.model.util.ThisReference;
import com.fortyoneconcepts.valjogen.processor.DiagnosticMessageConsumer;
import com.fortyoneconcepts.valjogen.processor.ProcessorMessages;
import com.fortyoneconcepts.valjogen.processor.TemplateKind;
import com.fortyoneconcepts.valjogen.processor.TypePool;

/**
 * This class assists the {@link ModelBuilder} with transforming qualified type names and types in the javax.lang.model.* format to our own valjogen models.
 *
 * Nb: Instances of this class is not multi-thread safe. Create a new for each thread.
 *
 * @author mmc
 */
final class TypeBuilder
{
	// private final static Logger LOGGER = Logger.getLogger(TypeBuilder.class.getName());

	private final TypePool typePool;
	private final Types types;
	private final Elements elements;
	private final DiagnosticMessageConsumer errorConsumer;
	private final TypeElement masterInterfaceElement;
	private final Configuration configuration;
	private final NoType noType;

	// private int recursiveCreateTypeCount = 0;

	TypeBuilder(Types types, Elements elements, DiagnosticMessageConsumer errorConsumer, TypeElement masterInterfaceElement, Configuration configuration, NoType noType)
	{
      this.types=types;
	  this.elements=elements;
	  this.errorConsumer=errorConsumer;
	  this.masterInterfaceElement=masterInterfaceElement;
	  this.configuration=configuration;

	  this.typePool = new TypePool();
	  this.noType = noType;
	}

	/**
	 * Create a new type or reuse existing if already created in order to save memoery and processing time.
	 *
	 * @param clazz The class that directly or indirectly references the type
	 * @param mirrorType corresponding javax.model type.
	 * @param detailLevel Decides how detailed the representaion should be.
	 *
	 * @return A new or resued Type instance.
	 */
	Type createType(BasicClazz clazz, TypeMirror mirrorType, DetailLevel detailLevel)
	{
		Type existingType = null;
		Type newType=null;

		try {
			String typeName = mirrorType.toString();

			// If using self-stand-in, replace with name of generated class and if identical with generate class return clazz itself as type
			typeName=typeName.replace(ThisReference.class.getName(), clazz.getGeneratedClazz().getPrototypicalQualifiedName());
			if (typeName.equals(clazz.getPrototypicalQualifiedName()))
				return clazz;

			existingType = typePool.get(clazz, typeName);
			boolean upgrade = existingType!=null && existingType.getDetailLevel().hasLowerDetailThen(detailLevel) && existingType.canBeMoreDetailed();
			if (existingType!=null && !upgrade) {
				assert(existingType.getClazz()==clazz);
				return existingType;
			}

			if (mirrorType instanceof javax.lang.model.type.PrimitiveType) {
				newType=new com.fortyoneconcepts.valjogen.model.PrimitiveType(clazz, typeName);
				existingType=typePool.put(typeName, newType);
			} else if (mirrorType.getKind()==TypeKind.ARRAY) {
			   ArrayType arrayType = (ArrayType)mirrorType;
			   TypeMirror componentTypeMirror = arrayType.getComponentType();
		       Type componentType = createType(clazz, componentTypeMirror, detailLevel);
		       newType=new com.fortyoneconcepts.valjogen.model.ArrayType(clazz, typeName, componentType);
		       existingType=typePool.put(typeName, newType);
			} else {
 	  	        ObjectType newObjectType;
 	  	        if (detailLevel==DetailLevel.High && (mirrorType instanceof DeclaredType)) {
			  	   newType=newObjectType=new com.fortyoneconcepts.valjogen.model.BasicClazz(clazz, configuration, typeName, (c) -> clazz.getHelperTypes());
			    } else {
			       newType=newObjectType=new com.fortyoneconcepts.valjogen.model.ObjectType(clazz, typeName);
				}

			    existingType=typePool.put(typeName, newType);
			    doInitObjectType(clazz, mirrorType, detailLevel, newObjectType);
			}

			assert upgrade || existingType==null : "Should not overwrite existing type in pool for type "+existingType.getQualifiedName()+" (unless in case of detail upgrade)";
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}

		return newType;
	}

	private void doInitObjectType(BasicClazz clazz, TypeMirror mirrorType, DetailLevel detailLevel, ObjectType newObjectType)
	{
	   List<DeclaredType> directSuperTypeMirrors = types.directSupertypes(mirrorType).stream().map(t -> (DeclaredType)t).collect(Collectors.toList());

	   DeclaredType baseClazzTypeMirror;
	   List<DeclaredType> interfaceSuperTypeMirrors;
	   List<DeclaredType> allSuperTypesWithAscendantsTypeMirrors;
	   List<? extends TypeMirror> genericTypeMirrorArguments;

	   if (directSuperTypeMirrors.size()>0) {
		   baseClazzTypeMirror = directSuperTypeMirrors.get(0);
		   interfaceSuperTypeMirrors = directSuperTypeMirrors.size()>1 ? directSuperTypeMirrors.subList(1, directSuperTypeMirrors.size()-1) : Collections.emptyList();
		   allSuperTypesWithAscendantsTypeMirrors = getSuperTypesWithAncestors(directSuperTypeMirrors).collect(Collectors.toList());
	   } else {
		   baseClazzTypeMirror=null;
		   interfaceSuperTypeMirrors=Collections.emptyList();
		   allSuperTypesWithAscendantsTypeMirrors=Collections.emptyList();
	   }

	   DeclaredType declaredType;
	   if (mirrorType instanceof DeclaredType) {
		   declaredType = (DeclaredType)mirrorType;
		   genericTypeMirrorArguments = declaredType.getTypeArguments();
	   } else {
		   declaredType=null;
		   genericTypeMirrorArguments=Collections.emptyList();
	   }

	   ObjectType baseClazzType = baseClazzTypeMirror!=null ? (ObjectType)createType(clazz, baseClazzTypeMirror, detailLevel) : noType;
	   List<Type> interfaceTypes = interfaceSuperTypeMirrors.stream().map(t -> createType(clazz, t, detailLevel)).collect(Collectors.toList());
	   Set<Type> interfaceTypesWithAscendants = allSuperTypesWithAscendantsTypeMirrors.stream().map(t -> createType(clazz, t, detailLevel)).collect(Collectors.toSet());
	   List<Type> genericTypeArguments = genericTypeMirrorArguments.stream().map(t -> createType(clazz, t, detailLevel)).collect(Collectors.toList());

	   newObjectType.initType(baseClazzType, interfaceTypes, interfaceTypesWithAscendants, genericTypeArguments);

	   if (newObjectType instanceof BasicClazz)
	   {
		  assert (declaredType!=null);
		  BasicClazz newClazzType = (BasicClazz)newObjectType;

		  Element newClazzElement=declaredType.asElement();

		  Stream<ExecutableElement> executableElements = newClazzElement.getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD || m.getKind()==ElementKind.CONSTRUCTOR).map(m -> (ExecutableElement)m).filter(m -> {
			Set<javax.lang.model.element.Modifier> modifiers = m.getModifiers();
			return !modifiers.contains(javax.lang.model.element.Modifier.PRIVATE);
		  });

		  List<Method> methods = executableElements.map(e -> createMethod(newClazzType, newClazzType, declaredType, e)).collect(Collectors.toList());

		  Stream<VariableElement> fieldElements = newClazzElement.getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.FIELD).map(m -> (VariableElement)m).filter(m -> {
			Set<javax.lang.model.element.Modifier> modifiers = m.getModifiers();
			return !modifiers.contains(javax.lang.model.element.Modifier.PRIVATE);
		  });

		  List<Member> members = fieldElements.map(e -> createMember(newClazzType, newClazzType, declaredType, e)).collect(Collectors.toList());

   	      EnumSet<Modifier> modifiers = createModifierSet(newClazzElement.getModifiers());

		  newClazzType.initContent(members,  methods, modifiers);
	   }
	}

	private Member createMember(BasicClazz clazz, Type declaringType, DeclaredType clazzDeclaredType, VariableElement fieldMirrorElement)
	{
		TypeMirror fieldMirrorType = fieldMirrorElement.asType();

		String fieldName = fieldMirrorElement.getSimpleName().toString();
		Type fieldType = createType(clazz, fieldMirrorType, DetailLevel.Low);
		EnumSet<Modifier> modifiers = createModifierSet(fieldMirrorElement.getModifiers());

		return new Member(clazz, fieldType, fieldName, modifiers);
	}

	private Method createMethod(BasicClazz clazz, Type declaringType, DeclaredType clazzDeclaredType, ExecutableElement methodMirrorElement)
	{
		ExecutableType methodMirrorType = (ExecutableType)methodMirrorElement.asType();

		String methodName = methodMirrorElement.getSimpleName().toString();

		TypeMirror returnTypeMirror = methodMirrorElement.getReturnType();

		Type returnType = createType(clazz, returnTypeMirror, DetailLevel.Low);

		EnumSet<Modifier> declaredModifiers = createModifierSet(methodMirrorElement.getModifiers());

		List<? extends VariableElement> params =  methodMirrorElement.getParameters();
		List<? extends TypeMirror> paramTypes = methodMirrorType.getParameterTypes();

		if (params.size()!=paramTypes.size())
			throw new RuntimeException("Internal error - Numbers of method parameters "+params.size()+" and method parameter types "+paramTypes.size()+" does not match");

		List<? extends TypeMirror> thrownTypeMirrors = methodMirrorType.getThrownTypes();
		List<Type> thrownTypes = thrownTypeMirrors.stream().map(ie -> createType(clazz, ie, DetailLevel.Low)).collect(Collectors.toList());

		List<Parameter> parameters = new ArrayList<Parameter>();
		for (int i=0; i<params.size(); ++i)
		{
			Parameter param = createParameter(clazz, params.get(i), paramTypes.get(i));
			parameters.add(param);
		}

		String javaDoc = "";

		Method newMethod;
		if (BuilderUtil.isConstructor(methodName))
		  newMethod=new Constructor(clazz, declaringType, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT);
		else newMethod = new Method(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT, TemplateKind.TYPED);

		return newMethod;
	}

	/**
	 * Our modifiers and the javax.lang model modifiers look the same but are different classes so we need to copy them
     *
	 * @param srcModifiers The javax.lang model modifier set.
	 *
	 * @return Our converted modifier set.
	 */
	EnumSet<Modifier> createModifierSet(Set<javax.lang.model.element.Modifier> srcModifiers)
	{
		if (srcModifiers.isEmpty())
			return EnumSet.noneOf(Modifier.class);

		HashSet<Modifier> dstSet = new HashSet<Modifier>();

		for (javax.lang.model.element.Modifier srcModifier : srcModifiers)
		{
			Modifier dstModifer = Modifier.valueOf(srcModifier.name());
			dstSet.add(dstModifer);
		}

		return EnumSet.copyOf(dstSet);
	}

	Parameter createParameter(BasicClazz clazz, VariableElement param, TypeMirror paramType)
	{
		String name = param.getSimpleName().toString();

		EnumSet<Modifier> modifiers = createModifierSet(param.getModifiers());

		return new Parameter(clazz, createType(clazz, paramType, DetailLevel.Low), createType(clazz, param.asType(), DetailLevel.Low), name, modifiers);
	}

	private Stream<DeclaredType> getSuperTypesWithAncestors(List<DeclaredType> superTypes)
	{
		return Stream.concat(superTypes.stream(), superTypes.stream().flatMap(type -> getSuperTypesWithAncestors(types.directSupertypes(type).stream().map(t -> (DeclaredType)t).collect(Collectors.toList()))));
	}

	HelperTypes createHelperTypes(BasicClazz clazz) throws Exception
	{
		Type voidType = new com.fortyoneconcepts.valjogen.model.PrimitiveType(clazz, "void");
		typePool.putIfAbsent("void", voidType);

		TypeMirror javaLangObjectMirrorType = createTypeFromString("java.lang.Object");
		ObjectType javaLangObjectType = (ObjectType)createType(clazz, javaLangObjectMirrorType, DetailLevel.Low);

		TypeMirror serializableInterfaceMirrorType = createTypeFromString("java.io.Serializable");
		ObjectType serializableInterfaceType = (ObjectType)createType(clazz, serializableInterfaceMirrorType, DetailLevel.Low);

		TypeMirror externalizableInterfaceMirrorType = createTypeFromString("java.io.Externalizable");
		ObjectType externalizableInterfaceType = (ObjectType)createType(clazz, externalizableInterfaceMirrorType, DetailLevel.Low);

		TypeMirror comparableInterfaceMirrorType = createTypeFromString("java.lang.Comparable");
		ObjectType comparableInterfaceType = (ObjectType)createType(clazz, comparableInterfaceMirrorType, DetailLevel.Low);

		TypeMirror javaUtilArraysMirrorType = createTypeFromString("java.util.Arrays");
		ObjectType javaUtilArraysType = (ObjectType)createType(clazz, javaUtilArraysMirrorType, DetailLevel.Low);

		TypeMirror javaUtilObjectsMirrorType = createTypeFromString("java.util.Objects");
		ObjectType javaUtilObjectsType = (ObjectType)createType(clazz, javaUtilObjectsMirrorType, DetailLevel.Low);

		TypeMirror generatedAnnotationInterfaceMirrorType = createTypeFromString("javax.annotation.Generated");
		Type generatedAnnotationInterfaceType = createType(clazz, generatedAnnotationInterfaceMirrorType, DetailLevel.Low);

		TypeMirror objectInputStreamMirrorType = createTypeFromString("java.io.ObjectInputStream");
		ObjectType inputStreamType = (ObjectType)createType(clazz, objectInputStreamMirrorType, DetailLevel.Low);

		TypeMirror objectOutputStreamMirrorType = createTypeFromString("java.io.ObjectOutputStream");
		ObjectType objectOutputStreamType = (ObjectType)createType(clazz, objectOutputStreamMirrorType, DetailLevel.Low);

		return new HelperTypes(noType, javaLangObjectType, voidType, serializableInterfaceType, externalizableInterfaceType, comparableInterfaceType, javaUtilArraysType, javaUtilObjectsType, generatedAnnotationInterfaceType, inputStreamType, objectOutputStreamType);
	}

	DeclaredType createBaseClazzDeclaredType(String clazzPackage) throws Exception
	{
		String baseClazzName = configuration.getBaseClazzName();
		if (baseClazzName==null || baseClazzName.isEmpty())
			baseClazzName=ConfigurationDefaults.RootObject;

		DeclaredType result = createDeclaredTypeFromString(baseClazzName, clazzPackage);

		if (result!=null && !BuilderUtil.isClass(result))
			errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.NOT_A_CLASS, result.toString()));

		return result;
	}

	TypeMirror createTypeFromString(String qualifiedName) throws Exception
	{
		TypeElement element = elements.getTypeElement(qualifiedName);
		if (element==null) {
			errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ClassNotFound, qualifiedName));
			return null; // Abort.
		}

		return element.asType();
	}

	DeclaredType createDeclaredTypeFromString(String name, String defaultPackageForUnqualifiedNames) throws Exception
	{
		String nameWithoutGenerics = NamesUtil.stripGenericQualifier(name);
		nameWithoutGenerics = defaultPackageForUnqualifiedNames!=null ? NamesUtil.ensureQualifedName(nameWithoutGenerics, defaultPackageForUnqualifiedNames) : null;

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
				nameGenericPart = NamesUtil.ensureQualifedName(nameGenericPart, defaultPackageForUnqualifiedNames);

				TypeElement genericElement = elements.getTypeElement(nameGenericPart);
				if (genericElement==null) {
					errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ClassNotFound, nameGenericPart));
					return (DeclaredType)element.asType(); // Recover.
				}

				TypeMirror genericElementType = genericElement.asType();
				genericTypeParts[i]=(DeclaredType)genericElementType;
			}

			DeclaredType result = types.getDeclaredType(element, genericTypeParts);
		    return result;
		}
	}

	List<DeclaredType> createInterfaceDeclaredTypes(DeclaredType masterInterfaceType, String[] ekstraInterfaceNames, String clazzPackage) throws Exception
	{
		List<DeclaredType> interfaceElements = new ArrayList<DeclaredType>();
		interfaceElements.add(masterInterfaceType);
		for (int i=0; i<ekstraInterfaceNames.length; ++i)
		{
			String ekstraInterfaceName = ekstraInterfaceNames[i];
			if (!ekstraInterfaceName.isEmpty())
			{
				DeclaredType extraDeclaredType = createDeclaredTypeFromString(ekstraInterfaceName, clazzPackage);
				interfaceElements.add(extraDeclaredType);

				if (!BuilderUtil.isInterface(extraDeclaredType))
					errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.NOT_AN_INTERFACE, extraDeclaredType.toString()));

			}
		}
		return interfaceElements;
	}

	Stream<DeclaredType> getDeclaredInterfacesWithAscendents(DeclaredType classOrInterfaceType)
	{
		TypeElement classOrInterfaceElement = (TypeElement)classOrInterfaceType.asElement();
		return Stream.concat(classOrInterfaceElement.getInterfaces().stream()
				             .map(t -> (DeclaredType)t)
				             .flatMap(z -> getDeclaredInterfacesWithAscendents(z)), Stream.of(classOrInterfaceType));
	}

	Stream<DeclaredType> getSuperTypesWithAscendents(DeclaredType classOrInterfaceType)
	{
		List<? extends TypeMirror> superTypes = types.directSupertypes(classOrInterfaceType);
		Stream<DeclaredType> superTypesAsDeclaredTypes = superTypes.stream().map(t -> (DeclaredType)t);
		return Stream.concat(superTypesAsDeclaredTypes, Stream.of(classOrInterfaceType));

	}
}
