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
 * $Log: TypeContext.java,v $
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

import fr.lifl.jaskell.compiler.core.Definition;

/**
 * An interface to allow communication between type expressions
 * and the context in which they are used.
 * <p>
 * This interface is used mainly within type package fr.lifl.to 
 * allow retrieval of context dependent information, most
 * notably type-classes instances declarations.
 *  
 * @author nono
 * @version $Id: TypeContext.java 1154 2005-11-24 21:43:37Z nono $
 */
public interface TypeContext {
	
	/**
	 * This method tries to locate a Definition for the
	 * given Type object according to <code>this</code> context.
	 * A Definition object or a null reference is returned.
	 * 
	 * @param t a Type to find Definition for
	 * @return Definition object or null if not found in context
	 */ 
	public Definition resolveType(Type  t);
}
