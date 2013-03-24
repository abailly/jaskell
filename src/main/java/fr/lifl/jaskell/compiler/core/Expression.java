package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

/**
 * @author bailly
 * @version $Id: Expression.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public interface Expression{

	/**
	 * Returns the tag object associated with the given name
	 * in this node
	 * 
	 * @return instance of a Tag or null
	 */
	public Tag getTag(String name);
	
	/**
	 * Store a tag object in this node
	 * 
	 * @param tag the tag to store. The <code>getName()</code> method
	 * of Tag interface is used as key 
	 */
	public void putTag(Tag tag);
	
	/**
	 * This method retrieves the type of an expression. The returned
	 * type may be null if type inference has not been done.
	 * 
	 * @return an object of a subclass of Type or null
	 */
	public Type getType();

	/**
	 * Sets the type of this expression to given type
	 * 
	 * @param type an object of a subclass of Type or null
	 */
	public void  setType(Type type);

	/**
	* Call back for visitor objects 
	 * 
	 * @param v a JaskellVisitor object
	 */
	public Object visit(JaskellVisitor v);
	
	/**
	 * Returns the enclosing context of this expression
	 * 
	 * @return the enclosing expression of this expression, 
	 * or null if expression is top-level
	 */
	public Expression getParent();
	
	/**
	 * Defines the enclosing context of this expression
	 * 
	 * @param parent parent expression for this expression
	 */
	public void setParent(Expression parent);
	/**
	 * Method resolve.
	 * @param vname
	 * @return Binding
	 */
	Expression lookup(String vname);


	
}
