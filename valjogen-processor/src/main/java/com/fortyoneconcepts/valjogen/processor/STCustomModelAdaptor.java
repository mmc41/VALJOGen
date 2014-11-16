/*
 * Custom ObjectModelAdapter that allows more prefixes
 * adapted with tiny additons from org.stringtemplate.v4.misc.ObjectModelAdaptor
 *
 *
 * [The "BSD license"]
 *  Copyright (c) 2011 Terence Parr
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.fortyoneconcepts.valjogen.processor;

import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;

import com.fortyoneconcepts.valjogen.model.Type;

/**
 * ST model adapter that handles normal ST properies AND do the following magic conversions:
 *
 *  Type.exactType_xxx -&gt; {@link Type#isExactType}, with argument consisting og xxx with underscores replaced by dot.
 *  Type.ofType_xxx -&gt; {@link Type#isOfType}, with argument consisting og xxx with underscores replaced by dot.
 *  Type.instanceMethod_xxx -&gt; {@link Type#hasInstanceMethod},with argument consisting of the overload name format of xxx (i.e. underscores replaced by dot + paranthesis).
 *  Type.staticMethod_xxx -&gt; {@link Type#hasStaticMethod}, with argument consisting of the overload name format of xxx (i.e. underscores replaced by dot + paranthesis).
 *  Type.instanceMember_xxx -&gt; {@link Type#hasInstanceMember}, where xxx is used as argument.
 *  Type.staticMember_xxx -&gt; {@link Type#hasStaticMember}, where xxx is used as argument.
 *
 * @author mmc
 *
 */
public class STCustomModelAdaptor extends ObjectModelAdaptor
{
	public static final String magicExactTypeMethodPrefix="exactType";
	public static final String magicImplementsMethodPrefix="ofType";
	public static final String magicHasInstanceMethodMethodPrefix="instanceMethod";
	public static final String magicHasStaticMethodMethodPrefix="staticMethod";
	public static final String magicHasInstanceMemberMethodPrefix="instanceMember";
	public static final String magicHasStaticMemberMethodPrefix="staticMember";

	@Override
	public Object getProperty(Interpreter interp, ST self, Object o, Object property, String propertyName)
	{
		if (o == null) {
			throw new NullPointerException("o");
		}

		Class<?> c = o.getClass();

		if ( property==null ) {
			return throwNoSuchProperty(c, propertyName, null);
		}

		// Check for magic property references
		if (com.fortyoneconcepts.valjogen.model.Type.class.isAssignableFrom(c))
		{
			com.fortyoneconcepts.valjogen.model.Type ot = (com.fortyoneconcepts.valjogen.model.Type)o;
			if (propertyName.startsWith(magicExactTypeMethodPrefix)) {
				String interfaceName = propertyName.substring(magicExactTypeMethodPrefix.length());
				if (interfaceName.startsWith("_"))
					interfaceName=interfaceName.substring(1);
				interfaceName=interfaceName.replace('_', '.');
				return ot.isExactType(interfaceName);
			} else if (propertyName.startsWith(magicImplementsMethodPrefix)) {
				String interfaceName = propertyName.substring(magicImplementsMethodPrefix.length());
				if (interfaceName.startsWith("_"))
					interfaceName=interfaceName.substring(1);
				interfaceName=interfaceName.replace('_', '.');
				return ot.isOfType(interfaceName);
			} else if (propertyName.startsWith(magicHasInstanceMethodMethodPrefix)) {
				String methodName = propertyName.substring(magicHasInstanceMethodMethodPrefix.length());
				if (methodName.startsWith("_"))
					methodName=methodName.substring(1);
				methodName=STUtil.templateNameToMethodName(methodName);
				return ot.hasInstanceMethod(methodName);
			} else if (propertyName.startsWith(magicHasStaticMethodMethodPrefix)) {
				String methodName = propertyName.substring(magicHasStaticMethodMethodPrefix.length());
				if (methodName.startsWith("_"))
					methodName=methodName.substring(1);
				methodName=STUtil.templateNameToMethodName(methodName);
				return ot.hasStaticMethod(methodName);
			} else if (propertyName.startsWith(magicHasInstanceMemberMethodPrefix)) {
				String memberName = propertyName.substring(magicHasInstanceMemberMethodPrefix.length());
				if (memberName.startsWith("_"))
					memberName=memberName.substring(1);
				return ot.hasInstanceMember(memberName);
			} else if (propertyName.startsWith(magicHasStaticMemberMethodPrefix)) {
				String memberName = propertyName.substring(magicHasStaticMemberMethodPrefix.length());
				if (memberName.startsWith("_"))
					memberName=memberName.substring(1);
				return ot.hasStaticMember(memberName);
			}
		}

		// Fall back on default behavior for non-magic properties.
		return super.getProperty(interp, self, o, property, propertyName);
	}
}
