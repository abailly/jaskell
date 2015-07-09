/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 *
 
 *
 */
package fr.lifl.jaskell.compiler.core;

import fr.lifl.jaskell.compiler.JaskellVisitor;
import fr.lifl.jaskell.compiler.types.Type;
import fr.lifl.parsing.Namespace;
import fr.lifl.parsing.SymbolException;

import java.util.*;
import java.util.logging.Logger;


/**
 * this class holds all declarations from a module A Module is a namespace which can contains Jaskell-Core declarations
 * and other modules. There exists one default module named Main which is created whether or not a real module named
 * Main appears in the source code.
 * <p/>
 * <p>Modules are named hierarchically like Java packages with '.' as separator in identifiers.
 *
 * @author bailly
 * @version $Id: Module.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Module implements Expression, Namespace {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers 
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(Module.class.getName());

    /*
     * This map holds all modules which are defined at the top-level ie. without
     * paren
     */
    private static Map toplevels = new HashMap();

    /**
     * toplevel declaration for module Prelude
     */
    public static Module PRELUDE = new Module("Prelude", null);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* map from type names to their definitions */
    private Map typeMap = new HashMap();

    /**
     * this map holds name to objects mapping in this module
     */
    private Map symbols = new HashMap();

    /**
     * full name of this module
     */
    private String name;

    /**
     * A list of all modules with this module as parent
     */
    private List modules = new ArrayList();

    /**
     * Reference to the parent module of this module
     */
    private Module parent;

    /**
     * list of type definitions
     */
    private List typeDefinitions = new ArrayList();

    /* Map of imported modules and definitions */
    private Map imports = new HashMap();

    protected HashMap tags;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for Module.
     */
    public Module(String name, Module parent) {
        if (parent != null) {
            parent.addModule(name, this);
            this.name = parent.getName() + "." + name;
        } else {
            this.name = name;
            toplevels.put(name, this);
        }
        this.parent = parent;
        /* add Prelude to imports */
        if (!name.equals("Prelude"))
            imports.put("Prelude", PRELUDE);
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Returns the toplevels.
     *
     * @return Map
     */
    public static Map getToplevels() {
        return toplevels;
    }

    /**
     * Adds a new imported module to this module
     * <p/>
     * <p>The imported namespace is used to resolved unqualified symbols if lookup in current module failed.
     *
     * @param imp a module
     */
    public void addImport(Module imp) {
        imports.put(imp.getName(), imp);
    }

    /**
     * Adds a new imported module renamed with <code>name</code>
     * <p/>
     * <p>Symbols prefixed with the given name will be resolved against this module.
     *
     * @param name
     * @param imp
     */
    public void addImport(String name, Module imp) {
        imports.put(name, imp);
    }

    /**
     * Adds a module named name to this module. If name already exists as an used identifier in this Module, throws an
     * IllegalArgumentException
     *
     * @param name   name of module to add
     * @param module Module object to add. May not be null
     * @throws IllegalArgumentException if name is already used in this module
     */
    public void addModule(String name, Module module) {
        bind(name, module);
        modules.add(module);
    }

    /**
     * Returns the name of this module.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Lookup object in module by name This method retrieves an object stored in this module by its name. The name must
     * be an unqualified name and is looked for in this module
     *
     * @param name an unqualified name to look for
     * @return object referred by this name in this module or null if not found
     */
    public Object resolve(String name) {
        if (name.indexOf('.') == -1)
            return lookup(name);
        else {
            int idx = name.indexOf(".");
            String part = name.substring(0, idx);
            if (this.name.equals(part))
                return resolve(name.substring(idx + 1));
            /* look in submodules */
            try {
                Module mod = (Module) symbols.get(part);
                if (mod != null)
                    return mod.resolve(name.substring(idx + 1));
            } catch (ClassCastException cce) {
                /* do nothing */
            }
            /* delegate to parent  module */
            if (parent != null)
                return parent.resolve(name);
        }
        return null;
    }

    /**
     * Lookup object in module and parent module by name This method is like lookup(String) but delegates lookup to
     * parent module if name is not defined in current module
     *
     * @param name name of object to find
     * @return a Definition or null if name not defined in this or parents modules
     */
    public Object lookupDeep(String name) {
        Object obj = symbols.get(name);
        /* try named imports */
        if (obj == null)
            obj = imports.get(name);
        /* try parents */
        if ((obj == null) && (parent != null))
            return parent.lookupDeep(name);
        else
            return obj;
    }

    /**
     * @see jaskell.compiler.core.Namespace#lookup(String)
     */
    public Expression lookup(String name) {
        Expression e = (Expression) lookupDeep(name);
        if (e == null)
            /* try to find definition in imported modules */
            e = resolveImport(name);
        return e;
    }

    /**
     * Try to resolve a type definition
     *
     * @see jaskell.compiler.core.Namespace#lookup(String)
     */
    public Definition resolveType(String name) {
        Definition ddef = (Definition) typeMap.get(name);
        if (ddef == null) {
            /* try to find definition in imported modules */
            ddef = (Definition) resolveTypeImport(name);
            /* try parent modules */
            if ((ddef == null) && (parent != null))
                return (Definition) parent.resolveType(name);
        }
        return ddef;
    }

    /**
     * Binds an expression in this module
     *
     * @param name name of symbol to bind
     * @param e    an Expression to bind to a symbol
     */
    public void bind(String name, Object o) {
        Expression e = (Expression) o;
        // System.err.println("Binding " + name + " :: "+e.getType()+" in module " +
        // this.getName());
        if (name == null)
            throw new IllegalArgumentException("Cannot bind null name");
        if (symbols.get(name) != null) {
            throw new IllegalArgumentException("Trying to bind duplicate symbol " + name + " in module " + this.name);
        }

        e.setParent(this);
        symbols.put(name, e);
    }

    /**
     * Binds a name to a value in this module
     *
     * @param name  name of new symbol
     * @param value bound object
     */
    /*
     * public void bind(String name, Object value) { System.err.println("Binding " +
     * name + " in module " + this.getName()); Object obj = symbols.get(name); if
     * (obj != null) throw new IllegalArgumentException( "Name " + name + " is
     * already used in module " + getName()); if (obj instanceof Expression)
     * ((Expression) obj).setParent(this); symbols.put(name, value); }
     */

    /**
     * Binds value to name in this module. This method does not complains if name is already bound : it justs discards
     * the old definition
     *
     * @param name  name of symbol
     * @param value bound object
     * @return old bound value or null
     */
    public Object rebind(String name, Object value) {
        Object obj = symbols.get(name);
        symbols.put(name, value);
        return obj;
    }

    public Object visit(JaskellVisitor v) {
        return v.visit(this);
    }

    /**
     * Returns the modules.
     *
     * @return List
     */
    public List getModules() {
        return modules;
    }

    /**
     * Returns the parent.
     *
     * @return Module
     */
    public Expression getParent() {
        return parent;
    }

    /**
     * Returns the symbols.
     *
     * @return Map
     */
    public Map getSymbols() {
        return symbols;
    }

    /**
     * Sets the parent.
     *
     * @param parent The parent to set
     */
    public void setParent(Expression parent) {
        this.parent = (Module) parent;
    }

    /**
     * A module has no type
     *
     * @see jaskell.compiler.core.Expression#getType()
     */
    public Type getType() {
        return null;
    }

    /**
     * Sets the type of this expression to given type
     *
     * @param type an object of a subclass of Type or null
     */
    public void setType(Type type) {
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        Iterator it = symbols.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            Expression def = (Expression) entry.getValue();
            sb.append(name).append(" = ").append(def).append(";");
        }
        return sb.toString();
    }

    /**
     * Method getTypeDefinitions.
     */
    public List getTypeDefinitions() {
        return typeDefinitions;
    }

    public void addTypeDefinition(Definition def) {
        String unqname = def.getName().substring(def.getName().lastIndexOf('.') + 1);
        typeMap.put(unqname, def);
        typeDefinitions.add(def);
        def.setParent(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.core.Namespace#getBindings()
     */
    public Map getBindings() {
        return symbols;
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.core.Expression#getTag(java.lang.String)
     */
    public Tag getTag(String name) {
        if (tags == null)
            return null;
        else
            return (Tag) tags.get(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see jaskell.compiler.core.Expression#putTag(jaskell.compiler.core.Tag)
     */
    public void putTag(Tag tag) {
        if (tags == null)
            tags = new HashMap();
        tags.put(tag.getName(), tag);
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.lifl.parsing.Namespace#forward(java.lang.String, java.lang.Object)
     */
    public void forward(String name, Object definition) throws SymbolException {
        throw new SymbolException("Cannot forward " + name + " in Module");
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.lifl.parsing.Namespace#unbind(java.lang.String)
     */
    public Object unbind(String name) throws SymbolException {
        throw new SymbolException("Cannot unbind " + name +
                " in Module environment");
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.lifl.parsing.Namespace#getEnclosing()
     */
    public Namespace getEnclosing() {
        return (Namespace) parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see fr.lifl.parsing.Namespace#getAllBindings()
     */
    public Map getAllBindings() {
        return new HashMap(symbols);
    }

    /**
     * Method resolveImport. Resolve a name with respect to imported modules
     *
     * @param name
     * @return Expression
     */
    private Expression resolveImport(String name) {
        Iterator it = imports.values().iterator();
        while (it.hasNext()) {
            Expression e = (Expression) it.next();
            e = e.lookup(name);
            if (e != null)
                return e;
        }
        return null;
    }

    /**
     * Method resolveTypeImport. Resolve a type name with respect to imported modules
     *
     * @param name
     * @return Expression
     */
    private Definition resolveTypeImport(String name) {
        Iterator it = imports.values().iterator();
        while (it.hasNext()) {
            Expression e = (Expression) it.next();
            e = ((Module) e).resolveType(name);
            if (e != null)
                return (Definition) e;
        }
        return null;
    }
}
