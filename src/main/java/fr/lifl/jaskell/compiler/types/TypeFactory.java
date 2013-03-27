/*______________________________________________________________________________
 *
 * Copyright 2003 Arnaud Bailly - NORSYS/LIFL
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 * (2) Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * (3) The name of the author may not be used to endorse or promote
 *     products derived from this software without specific prior
 *     written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *______________________________________________________________________________
 *
 * Created on Oct 20, 2003
 * $Log: TypeFactory.java,v $
 * Revision 1.4  2004/02/19 14:55:01  nono
 * Integrated jaskell to FIDL
 * Added rules in grammar to handle messages
 *
 * Revision 1.3  2004/02/18 17:20:07  nono
 * suppressed type classes use and definitions
 *
 * Revision 1.2  2004/02/09 20:40:02  nono
 * mise a jour repository central
 *
 * Revision 1.1  2003/10/20 19:10:28  bailly
 * Refactore type constraints handling : added a ConstrainedType
 * Type subclass, changed code to handle adding of TypeConstraints,
 * made constraints as linked lists within a ConstrainedType,
 * changed unification and substitution
 *
 * Added a TypeFactory class as an interface to type expressions
 * creation.
 *
 */
package fr.lifl.jaskell.compiler.types;

import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

/**
 * A focal point for constructing type expressions.
 * 
 * This factory class exports various static methods to 
 * construct different type expressions : type variables,
 * cosntrained types, type applications, ...
 * 
 * @author nono
 * @version $Id: TypeFactory.java 1153 2005-11-24 20:47:55Z nono $
 */
public class TypeFactory {

	private static final String freshPrefix = "t";

	private static int freshIndex = 0;

	/* map of all bindings already defined - to create fresh variables */
	private static WeakHashMap bounds = new WeakHashMap();


    /**
	 * Method freshBinding.
	 * @return LocalBinding
	 */
	public static Type freshBinding() {
		String fresh = freshPrefix + freshIndex;
		while (bounds.get(fresh) != null)
			fresh = freshPrefix + ++freshIndex;
		return makeTypeVariable(fresh);
	}

	/**
	 * @param fresh
	 * @return
	 */
	public static Type makeTypeVariable(String fresh) {
		Type t = new TypeVariable(fresh);
		bounds.put(fresh, t);
		return t;
	}

	/**
	 * Reset start index for generating fresh names and cleans up 
	 * hashmap
	 */
	public static void reset() {
		bounds.clear();
		freshIndex = 0;
	}

}
