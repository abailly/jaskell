package fr.lifl.jaskell.runtime.types;

/**
 * A type that does nothing and which contains only one member.
 * 
 * 
 * @author bailly
 * @version $Id: Unit.java 1153 2005-11-24 20:47:55Z nono $
 *  */
public class Unit extends JValue {

	public static final Unit UNIT = new Unit();
	
	private Unit() {
	}
		
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "()";
	}

}
