/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.HashMap;
import java.util.Map;
// import java.util.logging.Logger;

import com.fortyoneconcepts.valjogen.model.BasicClazz;
import com.fortyoneconcepts.valjogen.model.Type;

/**
 * Pool for recycling type instances.
 *
 * Nb: Instances of this class is not multi-thread safe. Create a new for each thread.
 *
 * @author mmc
 */
public final class TypePool
{
	// private final static Logger LOGGER = Logger.getLogger(TypePool.class.getName());

	private final Map<String,Type> types;

	public TypePool()
	{
		this.types = new HashMap<String, Type>();
	}

	private static final String getKey(BasicClazz clazz, String typeName)
	{
		return clazz.getQualifiedName()+":"+typeName;
	}

	public Type get(BasicClazz clazz, String typeName)
	{
		String key = getKey(clazz, typeName);
		return types.get(key);
	}

	public Type put(Type type)
	{
		String key = getKey(type.getClazz(), type.getQualifiedName());
		return types.put(key, type);
	}

	public Type put(String typeName, Type type)
	{
		String key = getKey(type.getClazz(), typeName);
		return types.put(key, type);
	}

	public void putIfAbsent(String typeName, Type type)
	{
		String key = getKey(type.getClazz(), typeName);
		types.putIfAbsent(key, type);
	}
}
