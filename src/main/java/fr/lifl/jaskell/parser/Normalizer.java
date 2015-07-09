/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.lifl.jaskell.compiler.core.Abstraction;
import fr.lifl.jaskell.compiler.core.Definition;
import fr.lifl.jaskell.compiler.core.Expression;
import fr.lifl.jaskell.compiler.core.LocalBinding;
import fr.lifl.parsing.Namespace;
import fr.lifl.parsing.SymbolException;


/**
 * A class that transforms a list of equations into core expressions ready to be sent to code generator This class
 * transforms the output of Yyparser, that is a map of definition names to list of equations into a list of core
 * definitions which are then bound into the given context. Normalizer only performs syntactic transformations, that is
 * mainly :
 *
 * <ul>
 * <li>pattern matching transformation,</li>
 * <li>lambda lifting,</li>
 * </ul>
 *
 * @author  bailly
 * @version $Id: Normalizer.java 1154 2005-11-24 21:43:37Z nono $
 */
public class Normalizer {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields 
    //~ ----------------------------------------------------------------------------------------------------------------

    /* the location to normalize */
    private Namespace namespace;

    /* Matcher object */
    private Matcher matcher;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructor for Normalizer.
     */
    public Normalizer(Namespace b) {
        this.namespace = b;
        this.matcher = new Matcher();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Normalizing method. This method normalizes a Map from names to list of equations.
     *
     * @param map a Map associating names to list of equations
     */
    public void normalize(Map m) throws SymbolException {
        Iterator it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String name = (String) entry.getKey();
            List eqs = (List) entry.getValue();
            /*
             * check equations are consistent : same number of arguments and retrieve
             * a list of fresh variables
             */
            List vars = checkArguments(eqs);
            if (vars.size() == 0) { /* no arguments - a CAF */
                /* we store the last definition given */
                Equation eq = (Equation) eqs.get(eqs.size() - 1);
                Expression def = eq.getRhs();
                namespace.bind(name, def);

            } else { /* we have a function definition -> abstraction */
                Abstraction abs = new Abstraction();
                /* bind variables into abstraction */
                Iterator it2 = vars.iterator();
                while (it2.hasNext()) {
                    LocalBinding lb = (LocalBinding) it2.next();
                    abs.bind(lb);
                }
                /* create list of PatternMatch */
                List pms = new ArrayList();
                it2 = eqs.iterator();
                while (it2.hasNext()) {
                    Equation eq = (Equation) it2.next();
                    /* strip first argument of lhs */
                    List l = new ArrayList(eq.getLhs());
                    l.remove(0);
                    PatternMatch pm = new PatternMatch();
                    pm.patterns = l;
                    pm.expr = eq.getRhs();
                    pms.add(pm);
                }
                abs.setBody(matcher.match(vars, pms, null));
                /* bind abstraction */
                try {
                    namespace.bind(name, abs);
                } catch (SymbolException ill) {
                    /*
                     * name is used - may be it is a definition so we could use its type
                     * to set type for abs
                     */
                    Expression expr = (Expression) namespace.resolve(name);
                    if (expr instanceof Definition) {
                        abs.setType(expr.getType());
                        namespace.rebind(name, abs);
                    } else
                        throw ill;
                }
            }
        }
    }

    /**
     * A method that checks a set of equations are consistent and which builds a list of bindings
     *
     * @param  eqs
     *
     * @return List
     */
    private List checkArguments(List eqs) {
        int n = -1;
        Iterator it = eqs.iterator();
        while (it.hasNext()) {
            Equation eq = (Equation) it.next();
            int narg = eq.getLhs().size() - 1;
            if (n == -1)
                n = narg;
            else if (n != narg)
                throw new IllegalStateException("Inconsistent number of arguments : expected " + n + ", found " + narg);
        }
        /* build variable list */
        List l = new ArrayList();
        while (n-- > 0)
            l.add(LocalBinding.freshBinding());
        return l;
    }

}
