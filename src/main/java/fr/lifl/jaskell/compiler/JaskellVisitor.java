/**
 * Copyright Arnaud Bailly, 2003-2013. All Rights Reserved.
 * 
 
 *
 */
package fr.lifl.jaskell.compiler;

import fr.lifl.jaskell.compiler.core.*;
import fr.lifl.jaskell.compiler.datatypes.ConstructorDefinition;
import fr.lifl.jaskell.compiler.datatypes.DataDefinition;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveConstructor;
import fr.lifl.jaskell.compiler.datatypes.PrimitiveData;


/**
 * A visitor interface for fucntinal expressions
 *
 * @author  Arnaud.Bailly - bailly@lifl.fr
 * @version $Id: JaskellVisitor.java 1154 2005-11-24 21:43:37Z nono $
 */
public interface JaskellVisitor {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

    Object visit(Abstraction a);

    Object visit(Alternative a);

    Object visit(Application a);

    Object visit(BooleanLiteral a);

    Object visit(CharLiteral a);

    Object visit(Constructor a);

    Object visit(Definition a);

    Object visit(DoubleLiteral a);

    Object visit(FloatLiteral a);

    Object visit(IntegerLiteral a);

    Object visit(Module a);

    Object visit(StringLiteral a);

    Object visit(Variable a);

    Object visit(QualifiedVariable a);

    Object visit(ConstructorPattern a);

    Object visit(LocalBinding a);

    Object visit(PrimitiveFunction f);

    /**
     * Method visit.
     *
     * @param  conditional
     *
     * @return Object
     */
    Object visit(Conditional conditional);

    /**
     * Method visit.
     *
     * @param  ddef
     *
     * @return Object
     */
    Object visit(DataDefinition ddef);

    /**
     * Method visit.
     *
     * @param  cdef
     *
     * @return Object
     */
    Object visit(ConstructorDefinition cdef);

    /**
     * Method visit.
     *
     * @param  let
     *
     * @return Object
     */
    Object visit(Let let);

    Object visit(PrimitiveConstructor a);

    Object visit(PrimitiveData a);

}
