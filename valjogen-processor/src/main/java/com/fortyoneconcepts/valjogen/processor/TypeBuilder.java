package com.fortyoneconcepts.valjogen.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import com.fortyoneconcepts.valjogen.model.BasicClazz;
import com.fortyoneconcepts.valjogen.model.HelperTypes;
import com.fortyoneconcepts.valjogen.model.ObjectType;
import com.fortyoneconcepts.valjogen.model.Type;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.model.util.ThisReference;

/**
 * This class assists the {@link ModelBuilder} with transforming qualified type names and types in the javax.lang.model.* format to our own valjogen models.
 *
 * @author mmc
 */
final class TypeBuilder
{
	private final Map<String,Type> allTypesByPrototypicalFullName;
	private final Types types;
	private final Elements elements;
	private final DiagnosticMessageConsumer errorConsumer;
	private final TypeElement masterInterfaceElement;

	public TypeBuilder(Types types, Elements elements, DiagnosticMessageConsumer errorConsumer, TypeElement masterInterfaceElement)
	{
      this.types=types;
	  this.elements=elements;
	  this.errorConsumer=errorConsumer;
	  this.masterInterfaceElement=masterInterfaceElement;

	  this.allTypesByPrototypicalFullName = new HashMap<String, Type>();
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
	public Type createType(BasicClazz clazz, TypeMirror mirrorType, DetailLevel detailLevel)
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
	       Type componentType = createType(clazz, componentTypeMirror, detailLevel);
	       newType=new com.fortyoneconcepts.valjogen.model.ArrayType(clazz, typeName, componentType);
	       existingType=allTypesByPrototypicalFullName.put(typeName, newType);
		} else {
  	       ObjectType newObjectType;
  	       newType=newObjectType=new com.fortyoneconcepts.valjogen.model.ObjectType(clazz, typeName);
		   existingType=allTypesByPrototypicalFullName.put(typeName, newType);

		   initObjectType(clazz, mirrorType, detailLevel, newObjectType);
		}

		assert existingType==null : "Should not overwrite existing type in pool";

		return newType;
	}

	private void initObjectType(BasicClazz clazz, TypeMirror mirrorType, DetailLevel detailLevel, ObjectType newObjectType)
	{
	   List<? extends TypeMirror> directSuperTypeMirrors = types.directSupertypes(mirrorType);

	   Type baseClazzType;
	   List<Type> interfaceTypes;
	   Set<Type> interfaceTypesWithAscendants;
	   if (directSuperTypeMirrors.size()>0) {
		   TypeMirror baseClazzTypeMirror = directSuperTypeMirrors.get(0);
		   baseClazzType = createType(clazz, baseClazzTypeMirror, detailLevel);

		   List<? extends TypeMirror> interfaceSuperTypeMirrors = directSuperTypeMirrors.size()>1 ? directSuperTypeMirrors.subList(1, directSuperTypeMirrors.size()-1) : Collections.emptyList();
		   interfaceTypes = interfaceSuperTypeMirrors.stream().map(t -> createType(clazz, t, detailLevel)).collect(Collectors.toList());
		   Stream<? extends TypeMirror> interfaceTypesWithAscendantsTypeMirrors = getSuperTypesWithAncestors(interfaceSuperTypeMirrors);
		   interfaceTypesWithAscendants = interfaceTypesWithAscendantsTypeMirrors.map(t -> createType(clazz, t, detailLevel)).collect(Collectors.toSet());
	   } else {
		   baseClazzType=new com.fortyoneconcepts.valjogen.model.NoType(clazz);
		   interfaceTypes=Collections.emptyList();
		   interfaceTypesWithAscendants=Collections.emptySet();
	   }

	   List<Type> genericTypeArguments = Collections.emptyList();
	   if (mirrorType instanceof DeclaredType) {
		   DeclaredType declaredType = (DeclaredType)mirrorType;

		   List<? extends TypeMirror> genericTypeMirrorArguments = declaredType.getTypeArguments();

		   genericTypeArguments = genericTypeMirrorArguments.stream().map(t -> createType(clazz, t, detailLevel)).collect(Collectors.toList());
	   }

	   newObjectType.initType(baseClazzType, interfaceTypes, interfaceTypesWithAscendants, genericTypeArguments);
	}

	private Stream<? extends TypeMirror> getSuperTypesWithAncestors(List<? extends TypeMirror> superTypes)
	{
		return Stream.concat(superTypes.stream(), superTypes.stream().flatMap(type -> getSuperTypesWithAncestors(types.directSupertypes(type))));
	}

	public HelperTypes createHelperTypes(BasicClazz clazz) throws Exception
	{
		com.fortyoneconcepts.valjogen.model.NoType noType = new com.fortyoneconcepts.valjogen.model.NoType(clazz);

		Type voidType = new com.fortyoneconcepts.valjogen.model.PrimitiveType(clazz, "void");
		allTypesByPrototypicalFullName.putIfAbsent("void", voidType);

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

	TypeMirror createTypeFromString(String qualifiedName) throws Exception
	{
		TypeElement element = elements.getTypeElement(qualifiedName);
		if (element==null) {
			errorConsumer.message(masterInterfaceElement, Kind.ERROR, String.format(ProcessorMessages.ClassNotFound, qualifiedName));
			return null; // Abort.
		}

		return element.asType();
	}

	public DeclaredType createDeclaredTypeFromString(String name, String clazzPackage) throws Exception
	{
		String nameWithoutGenerics = NamesUtil.stripGenericQualifier(name);
		nameWithoutGenerics = clazzPackage!=null ? NamesUtil.ensureQualifedName(nameWithoutGenerics, clazzPackage) : null;

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
					return (DeclaredType)element.asType(); // Recover.
				}

				TypeMirror genericElementType = genericElement.asType();
				genericTypeParts[i]=(DeclaredType)genericElementType;
			}

			DeclaredType result = types.getDeclaredType(element, genericTypeParts);
		    return result;
		}
	}

	public List<DeclaredType> createInterfaceDeclaredTypes(DeclaredType masterInterfaceType, String[] ekstraInterfaceNames, String clazzPackage) throws Exception
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
