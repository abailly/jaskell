/*
 * Created on Jun 8, 2003
 * Copyright 2003 Arnaud Bailly
 */
package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

import java.util.HashMap;

/**
 * @author bailly
 * @version $Id: ExpressionBase.java 1154 2005-11-24 21:43:37Z nono $
 */
public abstract class ExpressionBase implements Expression, Cloneable {

  /** stores type */
  protected Type type;
  
  /** stores parent expression */
  protected Expression parent;

  /**
   * @see jaskell.compiler.core.Expression#getType()
   */
  public Type getType() {
    TypeTag tag = (TypeTag) getTag("type");
    if (tag != null)
      return tag.getType();
    else
      return null;
  }

  /**
   * @see jaskell.compiler.core.Expression#setType(Type)
   */
  public void setType(Type type) {
    if (type != null)
      type.setContext(new ExpressionTypeContext(this));
    putTag(new TypeTag(type));
  }

  /**
   * @see jaskell.compiler.core.Expression#getParent()
   */
  public Expression getParent() {
    return parent;
  }

  /**
   * @see jaskell.compiler.core.Expression#setParent(Expression)
   */
  public void setParent(Expression parent) {
    this.parent = parent;
  }

  /**
   * @see jaskell.compiler.core.Expression#lookup(String)
   */
  public Expression lookup(String vname) {
    if (parent != null)
      return parent.lookup(vname);
    return null;
  }

  /**
   * type expressions are used only at parsing stage. They should not
   * be visited
   * 
   * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
   */
  public Object visit(JaskellVisitor v) {
    throw new UnsupportedOperationException(
					    "No visit method is defined for class " + getClass());
  }

  /* (non-Javadoc)
   * @see java.lang.Object#clone()
   */
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  protected HashMap tags;

  /* (non-Javadoc)
   * @see jaskell.compiler.core.Expression#getTag(java.lang.String)
   */
  public Tag getTag(String name) {
    if (tags == null)
      return null;
    else
      return (Tag) tags.get(name);
  }

  /* (non-Javadoc)
   * @see jaskell.compiler.core.Expression#putTag(jaskell.compiler.core.Tag)
   */
  public void putTag(Tag tag) {
    if (tags == null)
      tags = new HashMap();
    tags.put(tag.getName(), tag);
  }

}
