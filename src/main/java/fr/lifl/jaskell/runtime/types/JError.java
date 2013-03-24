/*______________________________________________________________________________
 * 
 * Copyright 2003 Arnaud Bailly - NORSYS
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
 * --------------------------------------------------------------------
 * Created on Jun 22, 2003
 * $Log: JError.java,v $
 * Revision 1.1  2003/06/23 06:33:31  bailly
 * Debugging
 * Added JError primitive type to raise runtime exceptions
 * when no match is found
 *
 */
package fr.lifl.jaskell.runtime.types;

/**
 * A class that is used to generate runtime errors in Jaskell.
 * 
 * This subclass of {@link java.lang.Error} represents a runtime error
 * occuring in a  Jaskell programm such that unmatched argument in a 
 * case or unresolved links.
 * 
 * @author bailly
 * @version $Id: JError.java 1153 2005-11-24 20:47:55Z nono $
 */
public class JError extends RuntimeException implements JObject {

	/**
	 * 
	 */
	public JError() {
		super();
	}

	/**
	 * @param message
	 */
	public JError(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public JError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param cause
	 */
	public JError(Throwable cause) {
		super(cause);
	}

	/* (non-Javadoc)
	 * @see jaskell.runtime.types.JObject#eval()
	 */
	public JObject eval() {
		return this;
	}

}
