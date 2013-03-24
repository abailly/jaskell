package fr.lifl.jaskell.runtime.types;

import java.lang.reflect.Field;

/**
 * Base class for all concrete values
 * 
 * @author bailly
 * @version $Id: JValue.java 1153 2005-11-24 20:47:55Z nono $
 */
public abstract class JValue implements JObject,JFunction {
	
  	/* number of applied arguments for constructors */
  	private int nargs = 0;
  	
  	
  	/**
	 * Evaluates this objects, yielding a supposedly reduced
	 * object. The implementation iin JObject does nothing and
	 * returns this.
	 * 
	 * @return a JObject which is guaranteed to be the same object
	 * or a reduced form of this.
	 */
	public JObject eval() {
		return this;
	}

	/**
	 * Evaluates this object as an integer
	 * 
	 * @return an int resulting from the evaluation of this.
	 * @exception IllegalStateException if this object cannot be
	 * reduced to an int value
	 */
	public int asInt() {
		throw new IllegalStateException("JObject may not be evaluated as int");
	}

	/**
	 * Evaluates this object as a float
	 * 
	 * @return a float resulting from the evaluation of this.
	 * @exception IllegalStateException if this object cannot be
	 * reduced to a float value
	 */
	public float asFloat() {
		throw new IllegalStateException("JObject may not be evaluated as float");
	}

	/**
	 * Evaluates this object as a double
	 * 
	 * @return a double resulting from the evaluation of this.
	 * @exception IllegalStateException if this object cannot be
	 * reduced to a double value
	 */
	public double asDouble() {
		throw new IllegalStateException("JObject may not be evaluated as double");
	}

	/**
	 * Evaluates this object as a char
	 * 
	 * @return a char resulting from the evaluation of this.
	 * @exception IllegalStateException if this object cannot be
	 * reduced to a char value
	 */
	public char asChar() {
		throw new IllegalStateException("JObject may not be evaluated as char");
	}

	/**
	* Evaluates this object as a String
	* 
	* @return a String resulting from the evaluation of this.
	* @exception IllegalStateException if this object cannot be
	* reduced to a String value
	*/
	public java.lang.String asString() {
		throw new IllegalStateException("JObject may not be evaluated as java.lang.String");
	}

	/**
	 * Evaluates this object as a boolean
	 * 
	 * @return a boolean resulting from the evaluation of this.
	 * @exception IllegalStateException if this object cannot be
	 * reduced to a boolean value
	 */
	public boolean asBool() {
		throw new IllegalStateException("JObject may not be evaluated as boolean");
	}
	

  /* (non-Javadoc)
   * @see jaskell.runtime.types.JFunction#apply(jaskell.runtime.types.JObject)
   */
  public JObject apply(JObject obj) {
    /* retrieve field with name equals to nargs */
    String fn = "_"+nargs;
    Class cls = this.getClass();
    try {
      Field fld = cls.getField(fn);
      Class fcls = fld.getType();
      if(!fcls.equals(JObject.class)) {  /* strict arguments */
        /* handle unboxed types */
       if(fcls.equals(int.class)) {
          fld.set(this,new Integer(((JValue)obj).asInt()));
        } else if(fcls.equals(char.class)) {
          fld.set(this,new Character(((JValue)obj).asChar()));
        }else if(fcls.equals(float.class)) {
          fld.set(this,new Float(((JValue)obj).asFloat()));
        }else if(fcls.equals(double.class)) {
          fld.set(this,new Double(((JValue)obj).asDouble()));
        }else if(fcls.equals(boolean.class)) {
          fld.set(this,new Boolean(((JValue)obj).asBool()));
        }else if(fcls.equals(String.class)) {
          fld.set(this,((JValue)obj).asString());
        }else {
          fld.set(this,obj);
        }
      } else {
        fld.set(this,obj);        
      }
      nargs++;
    } catch (Exception e) {
      throw new 
      IllegalArgumentException("Too many arguments applied");
    }
    return this;
  }

  /* Returns an empty instance of this object
   * @see jaskell.runtime.types.JFunction#init()
   */
  public JFunction init() {
    Class cls = this.getClass();
    try {
      return (JFunction)cls.newInstance();
    } catch (Exception e) {
      	throw new IllegalStateException("Cannot create instance of "+cls.getName());
    }
  }
}
