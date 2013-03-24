package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class representing qualified variable occurences.
 * 
 * This class represents occurences of variable references with qualified
 * path prepended. The only difference lays in the resolution method which starts
 * from the first component of qualified name.
 * 
 * @author bailly
 * @version $Id: QualifiedVariable.java 1154 2005-11-24 21:43:37Z nono $
 *  */
public class QualifiedVariable extends Variable {

	/** list of path components */
	private List path = new ArrayList();

	/**
	 * Constructor for Variable.
	 * @param name
	 * @param type
	 */
	public QualifiedVariable(String name) {
		super(name);
	}

	/**
	 *Constructs a variable with given type
	 * 
	 * @param name name of variable
	 * @param type type of constructed objects
	 */
	public QualifiedVariable(String name, Type type) {
		super(name, type);
	}

	/**
	 * Returns the path.
	 * @return Path
	 */
	public List getPath() {
		return path;
	}

	/**
	 * Adds a component to the path 
	 * @param path The path to set
	 */
	public void addPathElement(String elem) {
		this.path.add(elem);
	}

	/**
	 * @see jaskell.compiler.core.Expression#lookup(String)
	 */
	public Expression lookup(String vname) {
		Module mod = null;
		Iterator it = getPath().iterator();
		while (it.hasNext()) {
			String mname = (String) it.next();
			if (mod != null)
				mod = (Module) mod.lookup(mname);
			else
				mod = (Module) Module.getToplevels().get(mname);
		}
		// module found - locate and generate instructions
		if (mod != null)
			return mod.lookup(vname);
		return null;
	}

	/**
	 * @see jaskell.compiler.core.Expression#visit(JaskellVisitor)
	 */
	public Object visit(JaskellVisitor v) {
		return v.visit(this);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("");
		Iterator it = path.iterator();
		while (it.hasNext())
			sb.append(it.next()).append('.');
		return sb.append(getName()).toString();
	}

	public Object clone() {
		QualifiedVariable qv = new QualifiedVariable(getName(),getType());
		Iterator it =path.iterator();
		while(it.hasNext()) qv.addPathElement((String)it.next());
		qv.setParent(getParent());
		return qv;
	}
}