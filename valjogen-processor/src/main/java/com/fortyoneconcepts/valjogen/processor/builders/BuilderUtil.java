package com.fortyoneconcepts.valjogen.processor.builders;

import javax.lang.model.element.*;
import javax.lang.model.type.*;

class BuilderUtil
{
	static boolean isClass(TypeMirror typeMirror)
	{
		ElementKind kind = (typeMirror instanceof DeclaredType) ? ((DeclaredType) typeMirror).asElement().getKind() : ElementKind.OTHER;
		return kind==ElementKind.CLASS || kind==ElementKind.ENUM;
    }

	static boolean isInterface(TypeMirror typeMirror)
	{
		ElementKind kind = (typeMirror instanceof DeclaredType) ? ((DeclaredType) typeMirror).asElement().getKind() : ElementKind.OTHER;
        return kind == ElementKind.INTERFACE;
    }

	public static boolean isCallableConstructor(ExecutableElement constructor) {
	 if (constructor.getModifiers().contains(Modifier.PRIVATE)) {
	   return false;
	 }

	 TypeElement type = (TypeElement) constructor.getEnclosingElement();
	 return type.getEnclosingElement().getKind() == ElementKind.PACKAGE || type.getModifiers().contains(Modifier.STATIC);
	}

	public static boolean isConstructor(String methodName)
	{
		return methodName.equals("<init>");
	}
}
